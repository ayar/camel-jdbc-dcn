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
package com.eudemon.flow.engine.impl.event.logger.handler;

import java.util.Date;

import com.eudemon.flow.common.engine.api.delegate.event.FlowableEvent;
import com.eudemon.flow.common.engine.impl.interceptor.CommandContext;
import com.eudemon.flow.engine.entity.EventLogEntryEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Joram Barrez
 */
public interface EventLoggerEventHandler {

    EventLogEntryEntity generateEventLogEntry(CommandContext commandContext);

    void setEvent(FlowableEvent event);

    void setTimeStamp(Date timeStamp);

    void setObjectMapper(ObjectMapper objectMapper);

}
