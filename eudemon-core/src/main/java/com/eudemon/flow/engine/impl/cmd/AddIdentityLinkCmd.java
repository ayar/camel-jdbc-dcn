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

import com.eudemon.flow.common.engine.api.FlowableIllegalArgumentException;
import com.eudemon.flow.common.engine.impl.interceptor.CommandContext;
import com.eudemon.flow.engine.compatibility.Flowable5CompatibilityHandler;
import com.eudemon.flow.engine.impl.util.CommandContextUtil;
import com.eudemon.flow.engine.impl.util.Flowable5Util;
import com.eudemon.flow.engine.impl.util.IdentityLinkUtil;
import com.eudemon.flow.engine.impl.util.TaskHelper;
import com.eudemon.flow.identitylink.api.IdentityLinkType;
import com.eudemon.flow.identitylink.api.IdentityLinkEntity;
import com.eudemon.flow.task.api.TaskEntity;

/**
 * @author Joram Barrez
 */
public class AddIdentityLinkCmd extends NeedsActiveTaskCmd<Void> {

    private static final long serialVersionUID = 1L;

    public static int IDENTITY_USER = 1;
    public static int IDENTITY_GROUP = 2;

    protected String identityId;

    protected int identityIdType;

    protected String identityType;

    public AddIdentityLinkCmd(String taskId, String identityId, int identityIdType, String identityType) {
        super(taskId);
        validateParams(taskId, identityId, identityIdType, identityType);
        this.taskId = taskId;
        this.identityId = identityId;
        this.identityIdType = identityIdType;
        this.identityType = identityType;
    }

    protected void validateParams(String taskId, String identityId, int identityIdType, String identityType) {
        if (taskId == null) {
            throw new FlowableIllegalArgumentException("taskId is null");
        }

        if (identityType == null) {
            throw new FlowableIllegalArgumentException("type is required when adding a new task identity link");
        }

        if (identityId == null && (identityIdType == IDENTITY_GROUP ||
                (!IdentityLinkType.ASSIGNEE.equals(identityType) && !IdentityLinkType.OWNER.equals(identityType)))) {

            throw new FlowableIllegalArgumentException("identityId is null");
        }

        if (identityIdType != IDENTITY_USER && identityIdType != IDENTITY_GROUP) {
            throw new FlowableIllegalArgumentException("identityIdType allowed values are 1 and 2");
        }
    }

    @Override
    protected Void execute(CommandContext commandContext, TaskEntity task) {

        if (task.getProcessDefinitionId() != null && Flowable5Util.isFlowable5ProcessDefinitionId(commandContext, task.getProcessDefinitionId())) {
            Flowable5CompatibilityHandler compatibilityHandler = Flowable5Util.getFlowable5CompatibilityHandler();
            compatibilityHandler.addIdentityLink(taskId, identityId, identityIdType, identityType);
            return null;
        }

        String oldAssigneeId = task.getAssignee();
        String oldOwnerId = task.getOwner();

        boolean assignedToNoOne = false;
        if (IdentityLinkType.ASSIGNEE.equals(identityType)) {

            if (oldAssigneeId == null && identityId == null) {
                return null;
            }

            if (oldAssigneeId != null && oldAssigneeId.equals(identityId)) {
                return null;
            }

            TaskHelper.changeTaskAssignee(task, identityId);
            assignedToNoOne = identityId == null;

        } else if (IdentityLinkType.OWNER.equals(identityType)) {

            if (oldOwnerId == null && identityId == null) {
                return null;
            }

            if (oldOwnerId != null && oldOwnerId.equals(identityId)) {
                return null;
            }

            TaskHelper.changeTaskOwner(task, identityId);
            assignedToNoOne = identityId == null;

        } else if (IDENTITY_USER == identityIdType) {
            IdentityLinkEntity identityLinkEntity = CommandContextUtil.getIdentityLinkService().createTaskIdentityLink(task.getId(), identityId, null, identityType);
            IdentityLinkUtil.handleTaskIdentityLinkAddition(task, identityLinkEntity);

        } else if (IDENTITY_GROUP == identityIdType) {
            IdentityLinkEntity identityLinkEntity = CommandContextUtil.getIdentityLinkService().createTaskIdentityLink(task.getId(), null, identityId, identityType);
            IdentityLinkUtil.handleTaskIdentityLinkAddition(task, identityLinkEntity);

        }

        boolean forceNullUserId = false;
        if (assignedToNoOne) {
            // ACT-1317: Special handling when assignee is set to NULL, a
            // CommentEntity notifying of assignee-delete should be created
            forceNullUserId = true;
            if (IdentityLinkType.ASSIGNEE.equals(identityType)) {
                identityId = oldAssigneeId;
            } else {
                identityId = oldOwnerId;
            }
        }

        if (IDENTITY_USER == identityIdType) {
            CommandContextUtil.getHistoryManager(commandContext).createUserIdentityLinkComment(task, identityId, identityType, true, forceNullUserId);
        } else {
            CommandContextUtil.getHistoryManager(commandContext).createGroupIdentityLinkComment(task, identityId, identityType, true);
        }

        return null;
    }

}