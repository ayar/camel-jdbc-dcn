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

import com.eudemon.flow.engine.impl.cfg.ProcessEngineConfigurationImpl;
import com.eudemon.flow.engine.entity.PropertyEntity;
import com.eudemon.flow.engine.entity.PropertyEntityImpl;
import com.eudemon.flow.engine.entity.data.AbstractProcessDataManager;
import com.eudemon.flow.engine.entity.data.PropertyDataManager;

/**
 * @author Joram Barrez
 */
public class MybatisPropertyDataManager extends AbstractProcessDataManager<PropertyEntity> implements PropertyDataManager {

    public MybatisPropertyDataManager(ProcessEngineConfigurationImpl processEngineConfiguration) {
        super(processEngineConfiguration);
    }

    @Override
    public Class<? extends PropertyEntity> getManagedEntityClass() {
        return PropertyEntityImpl.class;
    }

    @Override
    public PropertyEntity create() {
        return new PropertyEntityImpl();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PropertyEntity> findAll() {
        return getDbSqlSession().selectList("selectProperties");
    }

}
