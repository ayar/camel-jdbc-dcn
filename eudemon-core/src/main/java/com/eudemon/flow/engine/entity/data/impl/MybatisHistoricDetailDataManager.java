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
package com.eudemon.flow.engine.entity.data.impl;

import java.util.List;
import java.util.Map;

import com.eudemon.flow.engine.history.HistoricDetail;
import com.eudemon.flow.engine.impl.HistoricDetailQueryImpl;
import com.eudemon.flow.engine.impl.cfg.ProcessEngineConfigurationImpl;
import com.eudemon.flow.engine.entity.HistoricDetailAssignmentEntity;
import com.eudemon.flow.engine.entity.HistoricDetailAssignmentEntityImpl;
import com.eudemon.flow.engine.entity.HistoricDetailEntity;
import com.eudemon.flow.engine.entity.HistoricDetailEntityImpl;
import com.eudemon.flow.engine.entity.HistoricDetailVariableInstanceUpdateEntity;
import com.eudemon.flow.engine.entity.HistoricDetailVariableInstanceUpdateEntityImpl;
import com.eudemon.flow.engine.entity.HistoricFormPropertyEntity;
import com.eudemon.flow.engine.entity.HistoricFormPropertyEntityImpl;
import com.eudemon.flow.engine.entity.data.AbstractProcessDataManager;
import com.eudemon.flow.engine.entity.data.HistoricDetailDataManager;

/**
 * @author Joram Barrez
 */
public class MybatisHistoricDetailDataManager extends AbstractProcessDataManager<HistoricDetailEntity> implements HistoricDetailDataManager {

    public MybatisHistoricDetailDataManager(ProcessEngineConfigurationImpl processEngineConfiguration) {
        super(processEngineConfiguration);
    }

    @Override
    public Class<? extends HistoricDetailEntity> getManagedEntityClass() {
        return HistoricDetailEntityImpl.class;
    }

    @Override
    public HistoricDetailEntity create() {
        // Superclass is abstract
        throw new UnsupportedOperationException();
    }

    @Override
    public HistoricDetailAssignmentEntity createHistoricDetailAssignment() {
        return new HistoricDetailAssignmentEntityImpl();
    }

    @Override
    public HistoricDetailVariableInstanceUpdateEntity createHistoricDetailVariableInstanceUpdate() {
        return new HistoricDetailVariableInstanceUpdateEntityImpl();
    }

    @Override
    public HistoricFormPropertyEntity createHistoricFormProperty() {
        return new HistoricFormPropertyEntityImpl();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<HistoricDetailEntity> findHistoricDetailsByProcessInstanceId(String processInstanceId) {
        return getDbSqlSession().selectList("selectHistoricDetailByProcessInstanceId", processInstanceId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<HistoricDetailEntity> findHistoricDetailsByTaskId(String taskId) {
        return getDbSqlSession().selectList("selectHistoricDetailByTaskId", taskId);
    }

    @Override
    public long findHistoricDetailCountByQueryCriteria(HistoricDetailQueryImpl historicVariableUpdateQuery) {
        return (Long) getDbSqlSession().selectOne("selectHistoricDetailCountByQueryCriteria", historicVariableUpdateQuery);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<HistoricDetail> findHistoricDetailsByQueryCriteria(HistoricDetailQueryImpl historicVariableUpdateQuery) {
        return getDbSqlSession().selectList("selectHistoricDetailsByQueryCriteria", historicVariableUpdateQuery);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<HistoricDetail> findHistoricDetailsByNativeQuery(Map<String, Object> parameterMap) {
        return getDbSqlSession().selectListWithRawParameter("selectHistoricDetailByNativeQuery", parameterMap);
    }

    @Override
    public long findHistoricDetailCountByNativeQuery(Map<String, Object> parameterMap) {
        return (Long) getDbSqlSession().selectOne("selectHistoricDetailCountByNativeQuery", parameterMap);
    }

}
