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
package com.eudemon.flow.engine.impl.agenda;

import com.eudemon.flow.bpmn.model.BoundaryEvent;
import com.eudemon.flow.bpmn.model.FlowElement;
import com.eudemon.flow.bpmn.model.FlowNode;
import com.eudemon.flow.bpmn.model.ServiceTask;
import com.eudemon.flow.common.engine.api.FlowableException;
import com.eudemon.flow.common.engine.impl.interceptor.CommandContext;
import com.eudemon.flow.engine.impl.delegate.ActivityBehavior;
import com.eudemon.flow.engine.impl.delegate.TriggerableActivityBehavior;
import com.eudemon.flow.engine.impl.jobexecutor.AsyncTriggerJobHandler;
import com.eudemon.flow.engine.entity.ExecutionEntity;
import com.eudemon.flow.engine.impl.util.CommandContextUtil;
import com.eudemon.flow.job.service.JobService;
import com.eudemon.flow.job.api.JobEntity;

/**
 * Operation that triggers a wait state and continues the process, leaving that activity.
 *
 * The {@link ExecutionEntity} for this operations should be in a wait state (receive task for example) and have a {@link FlowElement} that has a behaviour that implements the
 * {@link TriggerableActivityBehavior}.
 *
 * @author Joram Barrez
 */
public class TriggerExecutionOperation extends AbstractOperation {

    protected boolean triggerAsync;

    public TriggerExecutionOperation(CommandContext commandContext, ExecutionEntity execution) {
        super(commandContext, execution);
    }

    public TriggerExecutionOperation(CommandContext commandContext, ExecutionEntity execution, boolean triggerAsync) {
        super(commandContext, execution);
        this.triggerAsync = triggerAsync;
    }

    @Override
    public void run() {
        FlowElement currentFlowElement = getCurrentFlowElement(execution);
        if (currentFlowElement instanceof FlowNode) {

            ActivityBehavior activityBehavior = (ActivityBehavior) ((FlowNode) currentFlowElement).getBehavior();
            if (activityBehavior instanceof TriggerableActivityBehavior) {

                if (currentFlowElement instanceof BoundaryEvent
                        || currentFlowElement instanceof ServiceTask) { // custom service task with no automatic leave (will not have a activity-start history entry in ContinueProcessOperation)
                    CommandContextUtil.getHistoryManager(commandContext).recordActivityStart(execution);
                }

                if(!triggerAsync) {
                    ((TriggerableActivityBehavior) activityBehavior).trigger(execution, null, null);
                }
                else {
                    JobService jobService = CommandContextUtil.getJobService();
                    JobEntity job = jobService.createJob();
                    job.setExecutionId(execution.getId());
                    job.setProcessInstanceId(execution.getProcessInstanceId());
                    job.setProcessDefinitionId(execution.getProcessDefinitionId());
                    job.setJobHandlerType(AsyncTriggerJobHandler.TYPE);

                    // Inherit tenant id (if applicable)
                    if(execution.getTenantId() != null) {
                        job.setTenantId(execution.getTenantId());
                    }

                    jobService.createAsyncJob(job, true);
                    jobService.scheduleAsyncJob(job);
                }


            } else {
                throw new FlowableException("Cannot trigger execution with id " + execution.getId()
                    + " : the activityBehavior " + activityBehavior.getClass() + " does not implement the "
                    + TriggerableActivityBehavior.class.getName() + " interface");

            }

        } else if (currentFlowElement == null) {
            throw new FlowableException("Cannot trigger execution with id " + execution.getId()
                    + " : no current flow element found. Check the execution id that is being passed "
                    + "(it should not be a process instance execution, but a child execution currently referencing a flow element).");

        } else {
            throw new FlowableException("Programmatic error: cannot trigger execution, invalid flowelement type found: "
                    + currentFlowElement.getClass().getName() + ".");

        }
    }

}
