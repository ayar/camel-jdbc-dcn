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

import com.eudemon.flow.bpmn.model.Process;
import com.eudemon.flow.common.engine.api.delegate.event.FlowableEventDispatcher;
import com.eudemon.flow.common.engine.impl.interceptor.CommandContext;
import com.eudemon.flow.engine.delegate.event.impl.FlowableEventBuilder;
import com.eudemon.flow.engine.impl.jobexecutor.AsyncContinuationJobHandler;
import com.eudemon.flow.engine.entity.ExecutionEntity;
import com.eudemon.flow.engine.impl.runtime.ProcessInstanceBuilderImpl;
import com.eudemon.flow.engine.impl.util.CommandContextUtil;
import com.eudemon.flow.engine.impl.util.ProcessDefinitionUtil;
import com.eudemon.flow.engine.runtime.ProcessInstance;
import com.eudemon.flow.job.service.JobService;
import com.eudemon.flow.job.api.JobEntity;

/**
 * author martin.grofcik
 */
public class StartProcessInstanceAsyncCmd extends StartProcessInstanceCmd {

    public StartProcessInstanceAsyncCmd(ProcessInstanceBuilderImpl processInstanceBuilder) {
        super(processInstanceBuilder);
    }

    @Override
    public ProcessInstance execute(CommandContext commandContext) {

        processInstanceHelper = CommandContextUtil.getProcessEngineConfiguration(commandContext).getProcessInstanceHelper();
        ExecutionEntity processInstance = (ExecutionEntity) processInstanceHelper.createProcessInstance(getProcessDefinition(commandContext), businessKey, processInstanceName,
            overrideDefinitionTenantId, predefinedProcessInstanceId, variables, transientVariables, callbackId, callbackType, false);
        ExecutionEntity execution = processInstance.getExecutions().get(0);
        Process process = ProcessDefinitionUtil.getProcess(processInstance.getProcessDefinitionId());

        processInstanceHelper.processAvailableEventSubProcesses(processInstance, process, commandContext);

        if (CommandContextUtil.getProcessEngineConfiguration().getEventDispatcher().isEnabled()) {
            FlowableEventDispatcher eventDispatcher = CommandContextUtil.getProcessEngineConfiguration().getEventDispatcher();
            eventDispatcher.dispatchEvent(FlowableEventBuilder.createProcessStartedEvent(execution, variables, false));
        }

        executeAsynchronous(execution);

        return processInstance;
    }

    protected void executeAsynchronous(ExecutionEntity execution) {
        JobService jobService = CommandContextUtil.getJobService();

        JobEntity job = jobService.createJob();
        job.setExecutionId(execution.getId());
        job.setProcessInstanceId(execution.getProcessInstanceId());
        job.setProcessDefinitionId(execution.getProcessDefinitionId());
        job.setJobHandlerType(AsyncContinuationJobHandler.TYPE);

        // Inherit tenant id (if applicable)
        if (execution.getTenantId() != null) {
            job.setTenantId(execution.getTenantId());
        }

        execution.getJobs().add(job);

        jobService.createAsyncJob(job, false);
        jobService.scheduleAsyncJob(job);
    }

}
