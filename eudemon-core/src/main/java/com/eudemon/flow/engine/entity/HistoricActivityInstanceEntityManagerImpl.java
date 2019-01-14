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

package com.eudemon.flow.engine.entity;

import java.util.List;
import java.util.Map;

import com.eudemon.flow.common.engine.impl.history.HistoryLevel;
import com.eudemon.flow.common.engine.impl.persistence.entity.data.DataManager;
import com.eudemon.flow.engine.history.HistoricActivityInstance;
import com.eudemon.flow.engine.impl.HistoricActivityInstanceQueryImpl;
import com.eudemon.flow.engine.impl.cfg.ProcessEngineConfigurationImpl;
import com.eudemon.flow.engine.entity.data.HistoricActivityInstanceDataManager;

/**
 * @author Tom Baeyens
 * @author Joram Barrez
 */
public class HistoricActivityInstanceEntityManagerImpl extends AbstractEntityManager<HistoricActivityInstanceEntity> implements HistoricActivityInstanceEntityManager {

    protected HistoricActivityInstanceDataManager historicActivityInstanceDataManager;

    public HistoricActivityInstanceEntityManagerImpl(ProcessEngineConfigurationImpl processEngineConfiguration, HistoricActivityInstanceDataManager historicActivityInstanceDataManager) {
        super(processEngineConfiguration);
        this.historicActivityInstanceDataManager = historicActivityInstanceDataManager;
    }

    @Override
    protected DataManager<HistoricActivityInstanceEntity> getDataManager() {
        return historicActivityInstanceDataManager;
    }

    @Override
    public List<HistoricActivityInstanceEntity> findUnfinishedHistoricActivityInstancesByExecutionAndActivityId(String executionId, String activityId) {
        return historicActivityInstanceDataManager.findUnfinishedHistoricActivityInstancesByExecutionAndActivityId(executionId, activityId);
    }

    @Override
    public List<HistoricActivityInstanceEntity> findHistoricActivityInstancesByExecutionAndActivityId(String executionId, String activityId) {
        return historicActivityInstanceDataManager.findHistoricActivityInstancesByExecutionIdAndActivityId(executionId, activityId);
    }

    @Override
    public List<HistoricActivityInstanceEntity> findUnfinishedHistoricActivityInstancesByProcessInstanceId(String processInstanceId) {
        return historicActivityInstanceDataManager.findUnfinishedHistoricActivityInstancesByProcessInstanceId(processInstanceId);
    }

    @Override
    public void deleteHistoricActivityInstancesByProcessInstanceId(String historicProcessInstanceId) {
        if (getHistoryManager().isHistoryLevelAtLeast(HistoryLevel.ACTIVITY)) {
            historicActivityInstanceDataManager.deleteHistoricActivityInstancesByProcessInstanceId(historicProcessInstanceId);
        }
    }

    @Override
    public long findHistoricActivityInstanceCountByQueryCriteria(HistoricActivityInstanceQueryImpl historicActivityInstanceQuery) {
        return historicActivityInstanceDataManager.findHistoricActivityInstanceCountByQueryCriteria(historicActivityInstanceQuery);
    }

    @Override
    public List<HistoricActivityInstance> findHistoricActivityInstancesByQueryCriteria(HistoricActivityInstanceQueryImpl historicActivityInstanceQuery) {
        return historicActivityInstanceDataManager.findHistoricActivityInstancesByQueryCriteria(historicActivityInstanceQuery);
    }

    @Override
    public List<HistoricActivityInstance> findHistoricActivityInstancesByNativeQuery(Map<String, Object> parameterMap) {
        return historicActivityInstanceDataManager.findHistoricActivityInstancesByNativeQuery(parameterMap);
    }

    @Override
    public long findHistoricActivityInstanceCountByNativeQuery(Map<String, Object> parameterMap) {
        return historicActivityInstanceDataManager.findHistoricActivityInstanceCountByNativeQuery(parameterMap);
    }

    public HistoricActivityInstanceDataManager getHistoricActivityInstanceDataManager() {
        return historicActivityInstanceDataManager;
    }

    public void setHistoricActivityInstanceDataManager(HistoricActivityInstanceDataManager historicActivityInstanceDataManager) {
        this.historicActivityInstanceDataManager = historicActivityInstanceDataManager;
    }

}
