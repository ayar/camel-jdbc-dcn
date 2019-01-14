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
package com.eudemon.flow.engine.impl.history.async.json.transformer;

import static com.eudemon.flow.job.service.impl.history.async.util.AsyncHistoryJsonUtil.getDateFromJson;
import static com.eudemon.flow.job.service.impl.history.async.util.AsyncHistoryJsonUtil.getStringFromJson;

import java.util.Collections;
import java.util.List;

import com.eudemon.flow.common.engine.api.delegate.event.FlowableEngineEventType;
import com.eudemon.flow.common.engine.impl.interceptor.CommandContext;
import com.eudemon.flow.engine.ProcessEngineConfiguration;
import com.eudemon.flow.engine.delegate.event.impl.FlowableEventBuilder;
import com.eudemon.flow.engine.impl.history.async.HistoryJsonConstants;
import com.eudemon.flow.engine.entity.HistoricActivityInstanceEntity;
import com.eudemon.flow.engine.entity.HistoricActivityInstanceEntityManager;
import com.eudemon.flow.engine.impl.util.CommandContextUtil;
import com.eudemon.flow.job.api.HistoryJobEntity;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class ActivityStartHistoryJsonTransformer extends AbstractHistoryJsonTransformer {

    @Override
    public List<String> getTypes() {
        return Collections.singletonList(HistoryJsonConstants.TYPE_ACTIVITY_START);
    }

    @Override
    public boolean isApplicable(ObjectNode historicalData, CommandContext commandContext) {
        return true;
    }

    @Override
    public void transformJson(HistoryJobEntity job, ObjectNode historicalData, CommandContext commandContext) {
        HistoricActivityInstanceEntityManager historicActivityInstanceEntityManager = CommandContextUtil.getProcessEngineConfiguration(commandContext).getHistoricActivityInstanceEntityManager();

        HistoricActivityInstanceEntity historicActivityInstanceEntity = historicActivityInstanceEntityManager.create();
        ProcessEngineConfiguration processEngineConfiguration = CommandContextUtil.getProcessEngineConfiguration(commandContext);
        if (processEngineConfiguration.isUsePrefixId()) {
            historicActivityInstanceEntity.setId(historicActivityInstanceEntity.getIdPrefix() + processEngineConfiguration.getIdGenerator().getNextId());
        } else {
            historicActivityInstanceEntity.setId(processEngineConfiguration.getIdGenerator().getNextId());
        }

        historicActivityInstanceEntity.setProcessDefinitionId(getStringFromJson(historicalData, HistoryJsonConstants.PROCESS_DEFINITION_ID));
        historicActivityInstanceEntity.setProcessInstanceId(getStringFromJson(historicalData, HistoryJsonConstants.PROCESS_INSTANCE_ID));
        historicActivityInstanceEntity.setExecutionId(getStringFromJson(historicalData, HistoryJsonConstants.EXECUTION_ID));
        historicActivityInstanceEntity.setActivityId(getStringFromJson(historicalData, HistoryJsonConstants.ACTIVITY_ID));
        historicActivityInstanceEntity.setActivityName(getStringFromJson(historicalData, HistoryJsonConstants.ACTIVITY_NAME));
        historicActivityInstanceEntity.setActivityType(getStringFromJson(historicalData, HistoryJsonConstants.ACTIVITY_TYPE));
        historicActivityInstanceEntity.setAssignee(getStringFromJson(historicalData, HistoryJsonConstants.ASSIGNEE));
        historicActivityInstanceEntity.setStartTime(getDateFromJson(historicalData, HistoryJsonConstants.START_TIME));
        historicActivityInstanceEntity.setTenantId(getStringFromJson(historicalData, HistoryJsonConstants.TENANT_ID));

        historicActivityInstanceEntityManager.insert(historicActivityInstanceEntity);
        dispatchEvent(commandContext, FlowableEventBuilder.createEntityEvent(
                FlowableEngineEventType.HISTORIC_ACTIVITY_INSTANCE_CREATED, historicActivityInstanceEntity));
    }

}
