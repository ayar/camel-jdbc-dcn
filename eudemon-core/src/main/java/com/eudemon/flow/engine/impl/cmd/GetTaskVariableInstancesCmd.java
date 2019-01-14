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
import java.util.Map;

import com.eudemon.flow.common.engine.api.FlowableIllegalArgumentException;
import com.eudemon.flow.common.engine.api.FlowableObjectNotFoundException;
import com.eudemon.flow.common.engine.impl.interceptor.Command;
import com.eudemon.flow.common.engine.impl.interceptor.CommandContext;
import com.eudemon.flow.engine.impl.util.CommandContextUtil;
import com.eudemon.flow.task.api.Task;
import com.eudemon.flow.task.api.TaskEntity;
import com.eudemon.flow.variable.api.persistence.entity.VariableInstance;

public class GetTaskVariableInstancesCmd implements Command<Map<String, VariableInstance>>, Serializable {

    private static final long serialVersionUID = 1L;
    protected String taskId;
    protected Collection<String> variableNames;
    protected boolean isLocal;

    public GetTaskVariableInstancesCmd(String taskId, Collection<String> variableNames, boolean isLocal) {
        this.taskId = taskId;
        this.variableNames = variableNames;
        this.isLocal = isLocal;
    }

    @Override
    public Map<String, VariableInstance> execute(CommandContext commandContext) {
        if (taskId == null) {
            throw new FlowableIllegalArgumentException("taskId is null");
        }

        TaskEntity task = CommandContextUtil.getTaskService().getTask(taskId);

        if (task == null) {
            throw new FlowableObjectNotFoundException("task " + taskId + " doesn't exist", Task.class);
        }

        Map<String, VariableInstance> variables = null;
        if (variableNames == null) {

            if (isLocal) {
                variables = task.getVariableInstancesLocal();
            } else {
                variables = task.getVariableInstances();
            }

        } else {
            if (isLocal) {
                variables = task.getVariableInstancesLocal(variableNames, false);
            } else {
                variables = task.getVariableInstances(variableNames, false);
            }

        }

        return variables;
    }
}
