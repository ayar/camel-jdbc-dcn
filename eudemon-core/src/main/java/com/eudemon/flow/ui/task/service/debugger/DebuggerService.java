package com.eudemon.flow.ui.task.service.debugger;

import org.apache.commons.lang3.StringUtils;
import com.eudemon.flow.common.engine.api.FlowableException;
import com.eudemon.flow.engine.HistoryService;
import com.eudemon.flow.engine.ManagementService;
import com.eudemon.flow.engine.RuntimeService;
import com.eudemon.flow.common.engine.api.delegate.Expression;
import com.eudemon.flow.common.engine.impl.el.ExpressionManager;
import com.eudemon.flow.common.engine.impl.interceptor.Command;
import com.eudemon.flow.common.engine.impl.scripting.ScriptingEngines;
import com.eudemon.flow.engine.event.EventLogEntry;
import com.eudemon.flow.engine.impl.cfg.ProcessEngineConfigurationImpl;
import com.eudemon.flow.engine.impl.context.Context;
import com.eudemon.flow.engine.entity.ExecutionEntity;
import com.eudemon.flow.engine.entity.ExecutionEntityImpl;
import com.eudemon.flow.engine.runtime.Execution;
import com.eudemon.flow.engine.runtime.ProcessDebugger;
import com.eudemon.flow.job.api.Job;
import com.eudemon.flow.variable.api.delegate.VariableScope;
import com.eudemon.flow.ui.task.model.debugger.BreakpointRepresentation;
import com.eudemon.flow.ui.task.model.debugger.ExecutionRepresentation;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static com.eudemon.flow.engine.impl.agenda.DebugContinueProcessOperation.HANDLER_TYPE_BREAK_POINT;

/**
 * This class implements basic methods for managing breakpoints
 *
 * @author martin.grofcik
 */
@Service
public class DebuggerService implements ProcessDebugger, ApplicationContextAware {

    protected List<BreakpointRepresentation> breakpoints = new ArrayList<>();
    protected ApplicationContext applicationContext;

    public void addBreakpoint(BreakpointRepresentation breakpointRepresentation) {
        assert breakpointRepresentation != null && isNotBlank(breakpointRepresentation.getActivityId());
        this.breakpoints.add(breakpointRepresentation);
    }

    public void removeBreakpoint(BreakpointRepresentation breakpointRepresentation) {
        assert breakpointRepresentation != null && isNotBlank(breakpointRepresentation.getActivityId());
        if (!this.breakpoints.remove(breakpointRepresentation)) {
            throw new FlowableException("Breakpoint is not set on the activityId");
        }
    }

    public List<BreakpointRepresentation> getBreakpoints() {
        return breakpoints;
    }

    public Collection<String> getBrokenExecutions(String activityId, String processInstanceId) {
        List<Job> brokenJobs = getManagementService().createSuspendedJobQuery().
                processInstanceId(processInstanceId).
                handlerType(HANDLER_TYPE_BREAK_POINT).
                list();

        ArrayList<String> executions = new ArrayList<>();
        for (Job brokenJob : brokenJobs) {
            Execution brokenJobExecution = getRuntimeService().createExecutionQuery().executionId(brokenJob.getExecutionId()).singleResult();
            if (activityId.equals(brokenJobExecution.getActivityId())) {
                executions.add(brokenJob.getExecutionId());
            }
        }
        return executions;
    }

    public List<EventLogEntry> getProcessInstanceEventLog(String processInstanceId) {
        return getManagementService().getEventLogEntriesByProcessInstanceId(processInstanceId);
    }

    public void continueExecution(String executionId) {
        Job job = getManagementService().createSuspendedJobQuery().handlerType(HANDLER_TYPE_BREAK_POINT).executionId(executionId).singleResult();
        if (job == null) {
            throw new FlowableException("No broken job found for execution '" + executionId + "'");
        }

        getManagementService().moveSuspendedJobToExecutableJob(job.getId());
        try {
            // wait until job is processed
            while (getManagementService().createJobQuery().jobId(job.getId()).count() > 0) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
        }
    }

    @Override
    public boolean isBreakpoint(Execution execution) {
        for (BreakpointRepresentation breakpoint : breakpoints) {
            if (breakpoint.getActivityId().equals(execution.getActivityId())) {
                if (StringUtils.isEmpty(breakpoint.getProcessDefinitionId())) {
                    return true;
                }

                if (Objects.equals(breakpoint.getProcessDefinitionId(), ((ExecutionEntity) execution).getProcessDefinitionId())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected ManagementService getManagementService() {
        return this.applicationContext.getBean(ManagementService.class);
    }

    protected RuntimeService getRuntimeService() {
        return this.applicationContext.getBean(RuntimeService.class);
    }

    protected HistoryService getHistoricService() {
        return this.applicationContext.getBean(HistoryService.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public List<DebuggerRestVariable> getExecutionVariables(String executionId) {
        List<Execution> executions = getRuntimeService().createExecutionQuery().executionId(executionId).list();
        if (executions.isEmpty()) {
            return getHistoricService().createHistoricVariableInstanceQuery().executionId(executionId).list().stream().
                    map(DebuggerRestVariable::new).
                    collect(Collectors.toList());
        }
        return getRuntimeService().getVariableInstances(executionId).values().stream().
                map(DebuggerRestVariable::new).
                collect(Collectors.toList());
    }

    public List<ExecutionRepresentation> getExecutions(String processInstanceId) {
        List<Execution> executions = getRuntimeService().createExecutionQuery().processInstanceId(processInstanceId).list();
        List<ExecutionRepresentation> executionRepresentations = new ArrayList<>(executions.size());
        for (Execution execution : executions) {
            executionRepresentations.add(new ExecutionRepresentation(execution.getId(), execution.getParentId(),
                    execution.getProcessInstanceId(), execution.getSuperExecutionId(), execution.getActivityId(),
                    execution.isSuspended(), execution.getTenantId()));
        }
        return executionRepresentations;
    }

    public Object evaluateExpression(final String executionId, final String expressionString) {
        final ProcessEngineConfigurationImpl processEngineConfiguration = this.applicationContext.getBean(ProcessEngineConfigurationImpl.class);
        return processEngineConfiguration.getManagementService().executeCommand(commandContext -> {
            ExpressionManager expressionManager = processEngineConfiguration.getExpressionManager();
            Expression expression = expressionManager.createExpression(expressionString);
            Execution execution = Context.getProcessEngineConfiguration().getExecutionEntityManager().findById(executionId);
            return expression.getValue((VariableScope) execution);
        });
    }

    public void evaluateScript(final String executionId, final String scriptLanguage, final String script) {
        getManagementService().executeCommand(
                (Command<Void>) commandContext -> {
                    ScriptingEngines scriptingEngines = Context.getProcessEngineConfiguration().getScriptingEngines();
                    Execution execution = Context.getProcessEngineConfiguration().getExecutionEntityManager().findById(executionId);
                    scriptingEngines.evaluate(script, scriptLanguage, (ExecutionEntityImpl) execution, false);
                    return null;
                }
        );
    }
}
