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

import com.eudemon.flow.common.engine.api.delegate.event.FlowableEngineEventType;
import com.eudemon.flow.common.engine.impl.interceptor.Command;
import com.eudemon.flow.common.engine.impl.interceptor.CommandContext;
import com.eudemon.flow.engine.compatibility.Flowable5CompatibilityHandler;
import com.eudemon.flow.engine.delegate.event.impl.FlowableEventBuilder;
import com.eudemon.flow.engine.entity.AttachmentEntity;
import com.eudemon.flow.engine.entity.ExecutionEntity;
import com.eudemon.flow.engine.impl.util.CommandContextUtil;
import com.eudemon.flow.engine.impl.util.Flowable5Util;
import com.eudemon.flow.task.api.TaskEntity;

/**
 * @author Tom Baeyens
 * @author Joram Barrez
 */
public class DeleteAttachmentCmd implements Command<Object>, Serializable {

    private static final long serialVersionUID = 1L;
    protected String attachmentId;

    public DeleteAttachmentCmd(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    @Override
    public Object execute(CommandContext commandContext) {
        AttachmentEntity attachment = CommandContextUtil.getAttachmentEntityManager().findById(attachmentId);

        String processInstanceId = attachment.getProcessInstanceId();
        String processDefinitionId = null;
        ExecutionEntity processInstance = null;
        if (attachment.getProcessInstanceId() != null) {
            processInstance = CommandContextUtil.getExecutionEntityManager(commandContext).findById(processInstanceId);
            if (processInstance != null) {
                processDefinitionId = processInstance.getProcessDefinitionId();
                if (Flowable5Util.isFlowable5ProcessDefinitionId(commandContext, processInstance.getProcessDefinitionId())) {
                    Flowable5CompatibilityHandler compatibilityHandler = Flowable5Util.getFlowable5CompatibilityHandler();
                    compatibilityHandler.deleteAttachment(attachmentId);
                    return null;
                }
            }
        }

        CommandContextUtil.getAttachmentEntityManager().delete(attachment, false);

        if (attachment.getContentId() != null) {
            CommandContextUtil.getByteArrayEntityManager().deleteByteArrayById(attachment.getContentId());
        }

        TaskEntity task = null;
        if (attachment.getTaskId() != null) {
            task = CommandContextUtil.getTaskService().getTask(attachment.getTaskId());
        }

        if (attachment.getTaskId() != null) {
            CommandContextUtil.getHistoryManager(commandContext).createAttachmentComment(task, processInstance, attachment.getName(), false);
        }

        if (CommandContextUtil.getProcessEngineConfiguration(commandContext).getEventDispatcher().isEnabled()) {
            CommandContextUtil.getProcessEngineConfiguration(commandContext).getEventDispatcher()
                    .dispatchEvent(FlowableEventBuilder.createEntityEvent(FlowableEngineEventType.ENTITY_DELETED, attachment, processInstanceId, processInstanceId, processDefinitionId));
        }
        return null;
    }

}
