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

import com.eudemon.flow.common.engine.api.FlowableException;
import com.eudemon.flow.common.engine.api.FlowableIllegalArgumentException;
import com.eudemon.flow.common.engine.api.FlowableObjectNotFoundException;
import com.eudemon.flow.common.engine.impl.interceptor.Command;
import com.eudemon.flow.common.engine.impl.interceptor.CommandContext;
import com.eudemon.flow.engine.compatibility.Flowable5CompatibilityHandler;
import com.eudemon.flow.engine.entity.ExecutionEntity;
import com.eudemon.flow.engine.impl.util.CommandContextUtil;
import com.eudemon.flow.engine.impl.util.Flowable5Util;
import com.eudemon.flow.engine.runtime.ProcessInstance;

public class SetProcessInstanceNameCmd implements Command<Void>, Serializable {

    private static final long serialVersionUID = 1L;

    protected String processInstanceId;
    protected String name;

    public SetProcessInstanceNameCmd(String processInstanceId, String name) {
        this.processInstanceId = processInstanceId;
        this.name = name;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        if (processInstanceId == null) {
            throw new FlowableIllegalArgumentException("processInstanceId is null");
        }

        ExecutionEntity execution = CommandContextUtil.getExecutionEntityManager(commandContext).findById(processInstanceId);

        if (execution == null) {

            if (CommandContextUtil.getProcessEngineConfiguration(commandContext).isFlowable5CompatibilityEnabled()) {
                Flowable5CompatibilityHandler compatibilityHandler = Flowable5Util.getFlowable5CompatibilityHandler();
                if (compatibilityHandler != null) {
                    ProcessInstance processInstance = compatibilityHandler.getProcessInstance(processInstanceId);
                    if (processInstance != null) {
                        compatibilityHandler.setProcessInstanceName(processInstance.getId(), name);
                        return null;
                    }
                }
            }

            throw new FlowableObjectNotFoundException("process instance " + processInstanceId + " doesn't exist", ProcessInstance.class);
        }

        if (!execution.isProcessInstanceType()) {
            throw new FlowableObjectNotFoundException("process instance " + processInstanceId + " doesn't exist, the given ID references an execution, though", ProcessInstance.class);
        }

        if (execution.isSuspended()) {
            throw new FlowableException("process instance " + processInstanceId + " is suspended, cannot set name");
        }

        // Actually set the name
        execution.setName(name);

        // Record the change in history
        CommandContextUtil.getHistoryManager(commandContext).recordProcessInstanceNameChange(execution, name);

        return null;
    }

}
