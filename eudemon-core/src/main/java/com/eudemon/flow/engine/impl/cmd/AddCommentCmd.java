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

import com.eudemon.flow.common.engine.api.FlowableException;
import com.eudemon.flow.common.engine.api.FlowableObjectNotFoundException;
import com.eudemon.flow.common.engine.impl.identity.Authentication;
import com.eudemon.flow.common.engine.impl.interceptor.Command;
import com.eudemon.flow.common.engine.impl.interceptor.CommandContext;
import com.eudemon.flow.engine.compatibility.Flowable5CompatibilityHandler;
import com.eudemon.flow.engine.entity.CommentEntity;
import com.eudemon.flow.engine.entity.ExecutionEntity;
import com.eudemon.flow.engine.impl.util.CommandContextUtil;
import com.eudemon.flow.engine.impl.util.Flowable5Util;
import com.eudemon.flow.engine.runtime.Execution;
import com.eudemon.flow.engine.task.Comment;
import com.eudemon.flow.engine.task.Event;
import com.eudemon.flow.task.api.Task;
import com.eudemon.flow.task.api.TaskEntity;

/**
 * @author Tom Baeyens
 */
public class AddCommentCmd implements Command<Comment> {

    protected String taskId;
    protected String processInstanceId;
    protected String type;
    protected String message;

    public AddCommentCmd(String taskId, String processInstanceId, String message) {
        this.taskId = taskId;
        this.processInstanceId = processInstanceId;
        this.message = message;
    }

    public AddCommentCmd(String taskId, String processInstanceId, String type, String message) {
        this.taskId = taskId;
        this.processInstanceId = processInstanceId;
        this.type = type;
        this.message = message;
    }

    @Override
    public Comment execute(CommandContext commandContext) {

        TaskEntity task = null;
        // Validate task
        if (taskId != null) {
            task = CommandContextUtil.getTaskService().getTask(taskId);

            if (task == null) {
                throw new FlowableObjectNotFoundException("Cannot find task with id " + taskId, Task.class);
            }

            if (task.isSuspended()) {
                throw new FlowableException(getSuspendedTaskException());
            }
        }

        ExecutionEntity execution = null;
        if (processInstanceId != null) {
            execution = CommandContextUtil.getExecutionEntityManager(commandContext).findById(processInstanceId);

            if (execution == null) {
                throw new FlowableObjectNotFoundException("execution " + processInstanceId + " doesn't exist", Execution.class);
            }

            if (execution.isSuspended()) {
                throw new FlowableException(getSuspendedExceptionMessage());
            }
        }

        String processDefinitionId = null;
        if (execution != null) {
            processDefinitionId = execution.getProcessDefinitionId();
        } else if (task != null) {
            processDefinitionId = task.getProcessDefinitionId();
        }

        if (processDefinitionId != null && Flowable5Util.isFlowable5ProcessDefinitionId(commandContext, processDefinitionId)) {
            Flowable5CompatibilityHandler compatibilityHandler = Flowable5Util.getFlowable5CompatibilityHandler();
            return compatibilityHandler.addComment(taskId, processInstanceId, type, message);
        }

        String userId = Authentication.getAuthenticatedUserId();
        CommentEntity comment = CommandContextUtil.getCommentEntityManager(commandContext).create();
        comment.setUserId(userId);
        comment.setType((type == null) ? CommentEntity.TYPE_COMMENT : type);
        comment.setTime(CommandContextUtil.getProcessEngineConfiguration(commandContext).getClock().getCurrentTime());
        comment.setTaskId(taskId);
        comment.setProcessInstanceId(processInstanceId);
        comment.setAction(Event.ACTION_ADD_COMMENT);

        String eventMessage = message.replaceAll("\\s+", " ");
        if (eventMessage.length() > 163) {
            eventMessage = eventMessage.substring(0, 160) + "...";
        }
        comment.setMessage(eventMessage);

        comment.setFullMessage(message);

        CommandContextUtil.getCommentEntityManager(commandContext).insert(comment);

        return comment;
    }

    protected String getSuspendedTaskException() {
        return "Cannot add a comment to a suspended task";
    }

    protected String getSuspendedExceptionMessage() {
        return "Cannot add a comment to a suspended execution";
    }
}
