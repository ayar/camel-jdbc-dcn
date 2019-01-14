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

package com.eudemon.flow.engine.impl.event;

import com.eudemon.flow.common.engine.impl.interceptor.CommandContext;
import com.eudemon.flow.engine.impl.agenda.ContinueProcessOperation;
import com.eudemon.flow.engine.entity.ExecutionEntity;
import com.eudemon.flow.engine.impl.util.CommandContextUtil;
import com.eudemon.flow.job.service.JobHandler;
import com.eudemon.flow.job.api.JobEntity;
import com.eudemon.flow.variable.api.delegate.VariableScope;

/**
 * Continue in the broken process execution
 *
 * @author martin.grofcik
 */
public class BreakpointJobHandler implements JobHandler {

    public static final String JOB_HANDLER_TYPE = "breakpoint";

    @Override
    public String getType() {
        return JOB_HANDLER_TYPE;
    }

    @Override
    public void execute(JobEntity job, String configuration, VariableScope variableScope, CommandContext commandContext) {
        ExecutionEntity executionEntity = (ExecutionEntity) variableScope;
        CommandContextUtil.getAgenda(commandContext).planOperation(new ContinueProcessOperation(commandContext, executionEntity, true, false), executionEntity);
    }
}
