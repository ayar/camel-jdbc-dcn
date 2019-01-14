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

import org.apache.commons.lang3.StringUtils;
import com.eudemon.flow.common.engine.impl.interceptor.CommandContext;
import com.eudemon.flow.engine.history.HistoricActivityInstance;
import com.eudemon.flow.engine.impl.history.async.HistoryJsonConstants;
import com.eudemon.flow.engine.entity.HistoricDetailEntityManager;
import com.eudemon.flow.engine.entity.HistoricFormPropertyEntity;
import com.eudemon.flow.engine.entity.data.HistoricDetailDataManager;
import com.eudemon.flow.engine.impl.util.CommandContextUtil;
import com.eudemon.flow.job.api.HistoryJobEntity;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class FormPropertiesSubmittedHistoryJsonTransformer extends AbstractHistoryJsonTransformer {

    @Override
    public List<String> getTypes() {
        return Collections.singletonList(HistoryJsonConstants.TYPE_FORM_PROPERTIES_SUBMITTED);
    }

    @Override
    public boolean isApplicable(ObjectNode historicalData, CommandContext commandContext) {
        String activityId = getStringFromJson(historicalData, HistoryJsonConstants.ACTIVITY_ID);
        if (StringUtils.isNotEmpty(activityId)) {
            HistoricActivityInstance activityInstance = findHistoricActivityInstance(commandContext,
                    getStringFromJson(historicalData, HistoryJsonConstants.EXECUTION_ID), activityId);

            if (activityInstance == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void transformJson(HistoryJobEntity job, ObjectNode historicalData, CommandContext commandContext) {
        HistoricDetailDataManager historicDetailDataManager = CommandContextUtil.getProcessEngineConfiguration(commandContext).getHistoricDetailDataManager();

        int counter = 1;
        while (true) {

            String propertyId = getStringFromJson(historicalData, HistoryJsonConstants.FORM_PROPERTY_ID + counter);
            if (StringUtils.isEmpty(propertyId)) {
                break;
            }

            HistoricFormPropertyEntity historicFormPropertyEntity = historicDetailDataManager.createHistoricFormProperty();
            historicFormPropertyEntity.setProcessInstanceId(getStringFromJson(historicalData, HistoryJsonConstants.PROCESS_INSTANCE_ID));
            historicFormPropertyEntity.setExecutionId(getStringFromJson(historicalData, HistoryJsonConstants.EXECUTION_ID));
            historicFormPropertyEntity.setTaskId(getStringFromJson(historicalData, HistoryJsonConstants.TASK_ID));
            historicFormPropertyEntity.setPropertyId(propertyId);
            historicFormPropertyEntity.setPropertyValue(getStringFromJson(historicalData, HistoryJsonConstants.FORM_PROPERTY_VALUE + counter));
            historicFormPropertyEntity.setTime(getDateFromJson(historicalData, HistoryJsonConstants.CREATE_TIME));

            String activityId = getStringFromJson(historicalData, HistoryJsonConstants.ACTIVITY_ID);
            if (StringUtils.isNotEmpty(activityId)) {
                HistoricActivityInstance activityInstance = findHistoricActivityInstance(commandContext,
                        getStringFromJson(historicalData, HistoryJsonConstants.EXECUTION_ID), activityId);

                historicFormPropertyEntity.setActivityInstanceId(activityInstance.getId());
            }

            HistoricDetailEntityManager historicDetailEntityManager = CommandContextUtil.getProcessEngineConfiguration(commandContext).getHistoricDetailEntityManager();
            historicDetailEntityManager.insert(historicFormPropertyEntity);

            counter++;
        }
    }

}
