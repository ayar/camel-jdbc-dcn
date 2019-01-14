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
package com.eudemon.flow.engine.delegate.event.impl;

import com.eudemon.flow.common.engine.api.delegate.event.FlowableEngineEventType;
import com.eudemon.flow.common.engine.impl.event.FlowableEngineEventImpl;
import com.eudemon.flow.common.engine.impl.interceptor.CommandContext;
import com.eudemon.flow.engine.delegate.DelegateExecution;
import com.eudemon.flow.engine.delegate.event.FlowableProcessEngineEvent;
import com.eudemon.flow.engine.impl.util.CommandContextUtil;

/**
 * @author Joram Barrez
 */
public class FlowableProcessEventImpl extends FlowableEngineEventImpl implements FlowableProcessEngineEvent {

    public FlowableProcessEventImpl(FlowableEngineEventType type) {
        super(type);
    }

    @Override
    public DelegateExecution getExecution() {
        if (executionId != null) {
            CommandContext commandContext = CommandContextUtil.getCommandContext();
            if (commandContext != null) {
                return CommandContextUtil.getExecutionEntityManager(commandContext).findById(executionId);
            }
        }
        return null;
    }

}