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

import com.eudemon.flow.common.engine.api.FlowableIllegalArgumentException;
import com.eudemon.flow.common.engine.api.FlowableObjectNotFoundException;
import com.eudemon.flow.common.engine.impl.interceptor.Command;
import com.eudemon.flow.common.engine.impl.interceptor.CommandContext;
import com.eudemon.flow.engine.compatibility.Flowable5CompatibilityHandler;
import com.eudemon.flow.engine.entity.ExecutionEntity;
import com.eudemon.flow.engine.entity.ExecutionEntityManager;
import com.eudemon.flow.engine.impl.util.CommandContextUtil;
import com.eudemon.flow.engine.impl.util.Flowable5Util;
import com.eudemon.flow.engine.impl.util.IdentityLinkUtil;

/**
 * @author Marcus Klimstra
 * @author Joram Barrez
 */
public class AddIdentityLinkForProcessInstanceCmd implements Command<Void>, Serializable {

    private static final long serialVersionUID = 1L;

    protected String processInstanceId;

    protected String userId;

    protected String groupId;

    protected String type;

    public AddIdentityLinkForProcessInstanceCmd(String processInstanceId, String userId, String groupId, String type) {
        validateParams(processInstanceId, userId, groupId, type);
        this.processInstanceId = processInstanceId;
        this.userId = userId;
        this.groupId = groupId;
        this.type = type;
    }

    protected void validateParams(String processInstanceId, String userId, String groupId, String type) {

        if (processInstanceId == null) {
            throw new FlowableIllegalArgumentException("processInstanceId is null");
        }

        if (type == null) {
            throw new FlowableIllegalArgumentException("type is required when adding a new process instance identity link");
        }

        if (userId == null && groupId == null) {
            throw new FlowableIllegalArgumentException("userId and groupId cannot both be null");
        }

    }

    @Override
    public Void execute(CommandContext commandContext) {

        ExecutionEntityManager executionEntityManager = CommandContextUtil.getExecutionEntityManager(commandContext);
        ExecutionEntity processInstance = executionEntityManager.findById(processInstanceId);

        if (processInstance == null) {
            throw new FlowableObjectNotFoundException("Cannot find process instance with id " + processInstanceId, ExecutionEntity.class);
        }

        if (Flowable5Util.isFlowable5ProcessDefinitionId(commandContext, processInstance.getProcessDefinitionId())) {
            Flowable5CompatibilityHandler compatibilityHandler = Flowable5Util.getFlowable5CompatibilityHandler();
            compatibilityHandler.addIdentityLinkForProcessInstance(processInstanceId, userId, groupId, type);
            return null;
        }

        IdentityLinkUtil.createProcessInstanceIdentityLink(processInstance, userId, groupId, type);
        CommandContextUtil.getHistoryManager(commandContext).createProcessInstanceIdentityLinkComment(processInstance, userId, groupId, type, true);

        return null;

    }

}
