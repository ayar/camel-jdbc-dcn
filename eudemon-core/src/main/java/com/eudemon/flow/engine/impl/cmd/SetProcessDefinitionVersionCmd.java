/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eudemon.flow.engine.impl.cmd;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.eudemon.flow.common.engine.api.FlowableException;
import com.eudemon.flow.common.engine.api.FlowableIllegalArgumentException;
import com.eudemon.flow.common.engine.api.FlowableObjectNotFoundException;
import com.eudemon.flow.common.engine.impl.interceptor.Command;
import com.eudemon.flow.common.engine.impl.interceptor.CommandContext;
import com.eudemon.flow.engine.history.HistoricProcessInstance;
import com.eudemon.flow.engine.impl.persistence.deploy.DeploymentManager;
import com.eudemon.flow.engine.entity.ExecutionEntity;
import com.eudemon.flow.engine.entity.ExecutionEntityManager;
import com.eudemon.flow.engine.impl.util.CommandContextUtil;
import com.eudemon.flow.engine.impl.util.ProcessDefinitionUtil;
import com.eudemon.flow.engine.repository.ProcessDefinition;
import com.eudemon.flow.engine.runtime.ProcessInstance;
import com.eudemon.flow.task.api.TaskEntity;

/**
 * {@link Command} that changes the process definition version of an existing process instance.
 *
 * Warning: This command will NOT perform any migration magic and simply set the process definition version in the database, assuming that the user knows, what he or she is doing.
 *
 * This is only useful for simple migrations. The new process definition MUST have the exact same activity id to make it still run.
 *
 * Furthermore, activities referenced by sub-executions and jobs that belong to the process instance MUST exist in the new process definition version.
 *
 * The command will fail, if there is already a {@link ProcessInstance} or {@link HistoricProcessInstance} using the new process definition version and the same business key as the
 * {@link ProcessInstance} that is to be migrated.
 *
 * If the process instance is not currently waiting but actively running, then this would be a case for optimistic locking, meaning either the version update or the "real work" wins, i.e., this is a
 * race condition.
 *
 * @see {http://forums.activiti.org/en/viewtopic.php?t=2918}
 * @author Falko Menge
 */
public class SetProcessDefinitionVersionCmd implements Command<Void>, Serializable {

    private static final long serialVersionUID = 1L;

    private final String processInstanceId;
    private final Integer processDefinitionVersion;

    public SetProcessDefinitionVersionCmd(String processInstanceId, Integer processDefinitionVersion) {
        if (processInstanceId == null || processInstanceId.length() < 1) {
            throw new FlowableIllegalArgumentException("The process instance id is mandatory, but '" + processInstanceId + "' has been provided.");
        }
        if (processDefinitionVersion == null) {
            throw new FlowableIllegalArgumentException("The process definition version is mandatory, but 'null' has been provided.");
        }
        if (processDefinitionVersion < 1) {
            throw new FlowableIllegalArgumentException("The process definition version must be positive, but '" + processDefinitionVersion + "' has been provided.");
        }
        this.processInstanceId = processInstanceId;
        this.processDefinitionVersion = processDefinitionVersion;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        // check that the new process definition is just another version of the same
        // process definition that the process instance is using
        ExecutionEntityManager executionManager = CommandContextUtil.getExecutionEntityManager(commandContext);
        ExecutionEntity processInstance = executionManager.findById(processInstanceId);
        if (processInstance == null) {
            throw new FlowableObjectNotFoundException("No process instance found for id = '" + processInstanceId + "'.", ProcessInstance.class);
        } else if (!processInstance.isProcessInstanceType()) {
            throw new FlowableIllegalArgumentException("A process instance id is required, but the provided id " + "'" + processInstanceId + "' " + "points to a child execution of process instance " + "'"
                    + processInstance.getProcessInstanceId() + "'. " + "Please invoke the " + getClass().getSimpleName() + " with a root execution id.");
        }

        DeploymentManager deploymentCache = CommandContextUtil.getProcessEngineConfiguration(commandContext).getDeploymentManager();
        ProcessDefinition currentProcessDefinition = deploymentCache.findDeployedProcessDefinitionById(processInstance.getProcessDefinitionId());

        ProcessDefinition newProcessDefinition = deploymentCache
                .findDeployedProcessDefinitionByKeyAndVersionAndTenantId(currentProcessDefinition.getKey(), processDefinitionVersion, currentProcessDefinition.getTenantId());

        validateAndSwitchVersionOfExecution(commandContext, processInstance, newProcessDefinition);

        // switch the historic process instance to the new process definition version
        CommandContextUtil.getHistoryManager(commandContext).recordProcessDefinitionChange(processInstanceId, newProcessDefinition.getId());

        // switch all sub-executions of the process instance to the new process definition version
        Collection<ExecutionEntity> childExecutions = executionManager.findChildExecutionsByProcessInstanceId(processInstanceId);
        for (ExecutionEntity executionEntity : childExecutions) {
            validateAndSwitchVersionOfExecution(commandContext, executionEntity, newProcessDefinition);
        }

        return null;
    }

    protected void validateAndSwitchVersionOfExecution(CommandContext commandContext, ExecutionEntity execution, ProcessDefinition newProcessDefinition) {
        // check that the new process definition version contains the current activity
        com.eudemon.flow.bpmn.model.Process process = ProcessDefinitionUtil.getProcess(newProcessDefinition.getId());
        if (execution.getActivityId() != null && process.getFlowElement(execution.getActivityId(), true) == null) {
            throw new FlowableException("The new process definition " + "(key = '" + newProcessDefinition.getKey() + "') " + "does not contain the current activity " + "(id = '"
                    + execution.getActivityId() + "') " + "of the process instance " + "(id = '" + processInstanceId + "').");
        }

        // switch the process instance to the new process definition version
        execution.setProcessDefinitionId(newProcessDefinition.getId());
        execution.setProcessDefinitionName(newProcessDefinition.getName());
        execution.setProcessDefinitionKey(newProcessDefinition.getKey());

        // and change possible existing tasks (as the process definition id is stored there too)
        List<TaskEntity> tasks = CommandContextUtil.getTaskService().findTasksByExecutionId(execution.getId());
        for (TaskEntity taskEntity : tasks) {
            taskEntity.setProcessDefinitionId(newProcessDefinition.getId());
            CommandContextUtil.getHistoryManager(commandContext).recordTaskInfoChange(taskEntity);
        }
    }

}
