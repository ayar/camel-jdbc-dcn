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
package com.eudemon.flow.dmn.engine.configurator;

import java.util.ArrayList;
import java.util.List;

import com.eudemon.flow.common.engine.api.FlowableException;
import com.eudemon.flow.common.engine.impl.AbstractEngineConfiguration;
import com.eudemon.flow.common.engine.impl.AbstractEngineConfigurator;
import com.eudemon.flow.common.engine.impl.EngineDeployer;
import com.eudemon.flow.common.engine.impl.interceptor.EngineConfigurationConstants;
import com.eudemon.flow.common.engine.impl.persistence.entity.Entity;
import com.eudemon.flow.dmn.engine.DmnEngine;
import com.eudemon.flow.dmn.engine.DmnEngineConfiguration;
import com.eudemon.flow.dmn.engine.deployer.DmnDeployer;
import com.eudemon.flow.dmn.engine.impl.cfg.StandaloneInMemDmnEngineConfiguration;
import com.eudemon.flow.dmn.engine.impl.db.EntityDependencyOrder;

/**
 * @author Tijs Rademakers
 * @author Joram Barrez
 */
public class DmnEngineConfigurator extends AbstractEngineConfigurator {

    protected DmnEngineConfiguration dmnEngineConfiguration;

    @Override
    public int getPriority() {
        return EngineConfigurationConstants.PRIORITY_ENGINE_DMN;
    }

    @Override
    protected List<EngineDeployer> getCustomDeployers() {
        List<EngineDeployer> deployers = new ArrayList<>();
        deployers.add(new DmnDeployer());
        return deployers;
    }

    @Override
    protected String getMybatisCfgPath() {
        return DmnEngineConfiguration.DEFAULT_MYBATIS_MAPPING_FILE;
    }

    @Override
    public void configure(AbstractEngineConfiguration engineConfiguration) {
        if (dmnEngineConfiguration == null) {
            dmnEngineConfiguration = new StandaloneInMemDmnEngineConfiguration();
        }

        initialiseCommonProperties(engineConfiguration, dmnEngineConfiguration);

        initDmnEngine();

        initServiceConfigurations(engineConfiguration, dmnEngineConfiguration);
    }

    @Override
    protected List<Class<? extends Entity>> getEntityInsertionOrder() {
        return EntityDependencyOrder.INSERT_ORDER;
    }

    @Override
    protected List<Class<? extends Entity>> getEntityDeletionOrder() {
        return EntityDependencyOrder.DELETE_ORDER;
    }

    protected synchronized DmnEngine initDmnEngine() {
        if (dmnEngineConfiguration == null) {
            throw new FlowableException("DmnEngineConfiguration is required");
        }

        return dmnEngineConfiguration.buildDmnEngine();
    }

    public DmnEngineConfiguration getDmnEngineConfiguration() {
        return dmnEngineConfiguration;
    }

    public DmnEngineConfigurator setDmnEngineConfiguration(DmnEngineConfiguration dmnEngineConfiguration) {
        this.dmnEngineConfiguration = dmnEngineConfiguration;
        return this;
    }

}
