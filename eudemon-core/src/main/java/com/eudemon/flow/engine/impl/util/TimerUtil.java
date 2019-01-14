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
package com.eudemon.flow.engine.impl.util;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import com.eudemon.flow.bpmn.model.BoundaryEvent;
import com.eudemon.flow.bpmn.model.BpmnModel;
import com.eudemon.flow.bpmn.model.Event;
import com.eudemon.flow.bpmn.model.FlowElement;
import com.eudemon.flow.bpmn.model.IntermediateCatchEvent;
import com.eudemon.flow.bpmn.model.TimerEventDefinition;
import com.eudemon.flow.common.engine.api.FlowableException;
import com.eudemon.flow.common.engine.api.delegate.Expression;
import com.eudemon.flow.common.engine.api.delegate.event.FlowableEngineEventType;
import com.eudemon.flow.common.engine.api.delegate.event.FlowableEventDispatcher;
import com.eudemon.flow.common.engine.impl.calendar.BusinessCalendar;
import com.eudemon.flow.common.engine.impl.calendar.CycleBusinessCalendar;
import com.eudemon.flow.common.engine.impl.calendar.DueDateBusinessCalendar;
import com.eudemon.flow.common.engine.impl.calendar.DurationBusinessCalendar;
import com.eudemon.flow.common.engine.impl.el.ExpressionManager;
import com.eudemon.flow.common.engine.impl.runtime.Clock;
import com.eudemon.flow.engine.delegate.event.impl.FlowableEventBuilder;
import com.eudemon.flow.engine.impl.cfg.ProcessEngineConfigurationImpl;
import com.eudemon.flow.engine.impl.jobexecutor.TimerEventHandler;
import com.eudemon.flow.engine.entity.ExecutionEntity;
import com.eudemon.flow.job.service.TimerJobService;
import com.eudemon.flow.job.service.event.impl.FlowableJobEventBuilder;
import com.eudemon.flow.job.api.JobEntity;
import com.eudemon.flow.job.api.TimerJobEntity;
import com.eudemon.flow.variable.api.delegate.VariableScope;
import com.eudemon.flow.variable.service.impl.el.NoExecutionVariableScope;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * @author Joram Barrez
 */
public class TimerUtil {

    /**
     * The event definition on which the timer is based.
     *
     * Takes in an optional execution, if missing the {@link NoExecutionVariableScope} will be used (eg Timer start event)
     */
    public static TimerJobEntity createTimerEntityForTimerEventDefinition(TimerEventDefinition timerEventDefinition, boolean isInterruptingTimer,
            ExecutionEntity executionEntity, String jobHandlerType, String jobHandlerConfig) {

        ProcessEngineConfigurationImpl processEngineConfiguration = CommandContextUtil.getProcessEngineConfiguration();

        String businessCalendarRef = null;
        Expression expression = null;
        ExpressionManager expressionManager = processEngineConfiguration.getExpressionManager();

        // ACT-1415: timer-declaration on start-event may contain expressions NOT
        // evaluating variables but other context, evaluating should happen nevertheless
        VariableScope scopeForExpression = executionEntity;
        if (scopeForExpression == null) {
            scopeForExpression = NoExecutionVariableScope.getSharedInstance();
        }

        if (StringUtils.isNotEmpty(timerEventDefinition.getTimeDate())) {

            businessCalendarRef = DueDateBusinessCalendar.NAME;
            expression = expressionManager.createExpression(timerEventDefinition.getTimeDate());

        } else if (StringUtils.isNotEmpty(timerEventDefinition.getTimeCycle())) {

            businessCalendarRef = CycleBusinessCalendar.NAME;
            expression = expressionManager.createExpression(timerEventDefinition.getTimeCycle());

        } else if (StringUtils.isNotEmpty(timerEventDefinition.getTimeDuration())) {

            businessCalendarRef = DurationBusinessCalendar.NAME;
            expression = expressionManager.createExpression(timerEventDefinition.getTimeDuration());
        }

        if (StringUtils.isNotEmpty(timerEventDefinition.getCalendarName())) {
            businessCalendarRef = timerEventDefinition.getCalendarName();
            Expression businessCalendarExpression = expressionManager.createExpression(businessCalendarRef);
            businessCalendarRef = businessCalendarExpression.getValue(scopeForExpression).toString();
        }

        if (expression == null) {
            throw new FlowableException("Timer needs configuration (either timeDate, timeCycle or timeDuration is needed) (" + timerEventDefinition.getId() + ")");
        }

        BusinessCalendar businessCalendar = processEngineConfiguration.getBusinessCalendarManager().getBusinessCalendar(businessCalendarRef);

        String dueDateString = null;
        Date duedate = null;

        Object dueDateValue = expression.getValue(scopeForExpression);
        if (dueDateValue instanceof String) {
            dueDateString = (String) dueDateValue;

        } else if (dueDateValue instanceof Date) {
            duedate = (Date) dueDateValue;

        } else if (dueDateValue instanceof DateTime) {
            // JodaTime support
            duedate = ((DateTime) dueDateValue).toDate();

        } else if (dueDateValue instanceof Duration) {
        	dueDateString = ((Duration) dueDateValue).toString();
        } else if (dueDateValue instanceof Instant) {
            duedate = Date.from((Instant) dueDateValue);

        } else if (dueDateValue != null) {
            throw new FlowableException("Timer '" + executionEntity.getActivityId()
                    + "' was not configured with a valid duration/time, either hand in a java.util.Date or a java.time.Instant or a org.joda.time.DateTime or a String in format 'yyyy-MM-dd'T'hh:mm:ss'");
        }

        if (duedate == null && dueDateString != null) {
            duedate = businessCalendar.resolveDuedate(dueDateString);
        }

        TimerJobEntity timer = null;
        if (duedate != null) {
            timer = CommandContextUtil.getTimerJobService().createTimerJob();
            timer.setJobType(JobEntity.JOB_TYPE_TIMER);
            timer.setRevision(1);
            timer.setJobHandlerType(jobHandlerType);
            timer.setJobHandlerConfiguration(jobHandlerConfig);
            timer.setExclusive(true);
            timer.setRetries(processEngineConfiguration.getAsyncExecutorNumberOfRetries());
            timer.setDuedate(duedate);
            if (executionEntity != null) {
                timer.setExecutionId(executionEntity.getId());
                timer.setProcessDefinitionId(executionEntity.getProcessDefinitionId());
                timer.setProcessInstanceId(executionEntity.getProcessInstanceId());

                // Inherit tenant identifier (if applicable)
                if (executionEntity.getTenantId() != null) {
                    timer.setTenantId(executionEntity.getTenantId());
                }
            }

        } else {
            throw new FlowableException("Due date could not be determined for timer job " + dueDateString);
        }

        if (StringUtils.isNotEmpty(timerEventDefinition.getTimeCycle())) {
            // See ACT-1427: A boundary timer with a cancelActivity='true', doesn't need to repeat itself
            boolean repeat = !isInterruptingTimer;

            // ACT-1951: intermediate catching timer events shouldn't repeat according to spec
            if (executionEntity != null) {
                FlowElement currentElement = executionEntity.getCurrentFlowElement();
                if (currentElement instanceof IntermediateCatchEvent) {
                    repeat = false;
                }
            }

            if (repeat) {
                String prepared = prepareRepeat(dueDateString);
                timer.setRepeat(prepared);
            }
        }

        if (timer != null && executionEntity != null) {
            timer.setExecutionId(executionEntity.getId());
            timer.setProcessDefinitionId(executionEntity.getProcessDefinitionId());
            timer.setProcessInstanceId(executionEntity.getProcessInstanceId());

            // Inherit tenant identifier (if applicable)
            if (executionEntity.getTenantId() != null) {
                timer.setTenantId(executionEntity.getTenantId());
            }
        }

        return timer;
    }

    public static TimerJobEntity rescheduleTimerJob(String timerJobId, TimerEventDefinition timerEventDefinition) {
        TimerJobService timerJobService = CommandContextUtil.getTimerJobService();
        TimerJobEntity timerJob = timerJobService.findTimerJobById(timerJobId);
        if (timerJob != null) {
            BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(timerJob.getProcessDefinitionId());
            Event eventElement = (Event) bpmnModel.getFlowElement(TimerEventHandler.getActivityIdFromConfiguration(timerJob.getJobHandlerConfiguration()));
            boolean isInterruptingTimer = false;
            if (eventElement instanceof BoundaryEvent) {
                isInterruptingTimer = ((BoundaryEvent) eventElement).isCancelActivity();
            }

            ExecutionEntity execution = CommandContextUtil.getExecutionEntityManager().findById(timerJob.getExecutionId());
            TimerJobEntity rescheduledTimerJob = TimerUtil.createTimerEntityForTimerEventDefinition(timerEventDefinition, isInterruptingTimer, execution,
                    timerJob.getJobHandlerType(), timerJob.getJobHandlerConfiguration());

            timerJobService.deleteTimerJob(timerJob);
            timerJobService.insertTimerJob(rescheduledTimerJob);

            FlowableEventDispatcher eventDispatcher = CommandContextUtil.getProcessEngineConfiguration().getEventDispatcher();
            if (eventDispatcher.isEnabled()) {
                eventDispatcher.dispatchEvent(
                        FlowableEventBuilder.createJobRescheduledEvent(FlowableEngineEventType.JOB_RESCHEDULED, rescheduledTimerJob, timerJob.getId()));

             // job rescheduled event should occur before new timer scheduled event
                eventDispatcher.dispatchEvent(
                                FlowableJobEventBuilder.createEntityEvent(FlowableEngineEventType.TIMER_SCHEDULED, rescheduledTimerJob));
            }

            return rescheduledTimerJob;
        }
        return null;
    }

    public static String prepareRepeat(String dueDate) {
        if (dueDate.startsWith("R") && dueDate.split("/").length == 2) {
            DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
            Clock clock = CommandContextUtil.getProcessEngineConfiguration().getClock();
            Date now = clock.getCurrentTime();
            return dueDate.replace("/", "/" + fmt.print(new DateTime(now,
                    DateTimeZone.forTimeZone(clock.getCurrentTimeZone()))) + "/");
        }
        return dueDate;
    }

}
