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
package com.eudemon.flow.cmmn.spring.configurator;

import com.eudemon.flow.cmmn.engine.CmmnEngine;
import com.eudemon.flow.cmmn.engine.configurator.CmmnEngineConfigurator;
import com.eudemon.flow.cmmn.spring.SpringCmmnEngineConfiguration;
import com.eudemon.flow.cmmn.spring.SpringCmmnExpressionManager;
import com.eudemon.flow.common.engine.api.FlowableException;
import com.eudemon.flow.common.engine.impl.AbstractEngineConfiguration;
import com.eudemon.flow.common.engine.impl.interceptor.EngineConfigurationConstants;
import com.eudemon.flow.common.spring.SpringEngineConfiguration;
import com.eudemon.flow.spring.SpringProcessEngineConfiguration;

/**
 * @author Tijs Rademakers
 * @author Joram Barrez
 */
public class SpringCmmnEngineConfigurator extends CmmnEngineConfigurator {

    @Override
    public void configure(AbstractEngineConfiguration engineConfiguration) {
        if (cmmnEngineConfiguration == null) {
            cmmnEngineConfiguration = new SpringCmmnEngineConfiguration();
        }

        if (!(cmmnEngineConfiguration instanceof SpringCmmnEngineConfiguration)) {
            throw new FlowableException("SpringCmmnEngineConfigurator accepts only SpringCmmnEngineConfiguration. " + cmmnEngineConfiguration.getClass().getName());
        }

        initialiseCommonProperties(engineConfiguration, cmmnEngineConfiguration);

        SpringEngineConfiguration springEngineConfiguration = (SpringEngineConfiguration) engineConfiguration;

        SpringProcessEngineConfiguration springProcessEngineConfiguration = null;
        if (springEngineConfiguration instanceof SpringProcessEngineConfiguration) {
            springProcessEngineConfiguration = (SpringProcessEngineConfiguration) springEngineConfiguration;
        } else {
            AbstractEngineConfiguration processEngineConfiguration = engineConfiguration.getEngineConfigurations().get(EngineConfigurationConstants.KEY_PROCESS_ENGINE_CONFIG);
            if (processEngineConfiguration instanceof SpringProcessEngineConfiguration) {
                springProcessEngineConfiguration = (SpringProcessEngineConfiguration) processEngineConfiguration;
            }
        }

        if (springProcessEngineConfiguration != null) {
           copyProcessEngineProperties(springProcessEngineConfiguration);
        }

        ((SpringCmmnEngineConfiguration) cmmnEngineConfiguration).setTransactionManager(springEngineConfiguration.getTransactionManager());
        cmmnEngineConfiguration.setExpressionManager(new SpringCmmnExpressionManager(
                        springEngineConfiguration.getApplicationContext(), springEngineConfiguration.getBeans()));

        initCmmnEngine();

        initServiceConfigurations(engineConfiguration, cmmnEngineConfiguration);
    }

    @Override
    protected synchronized CmmnEngine initCmmnEngine() {
        if (cmmnEngineConfiguration == null) {
            throw new FlowableException("CmmnEngineConfiguration is required");
        }

        return cmmnEngineConfiguration.buildCmmnEngine();
    }

    @Override
    public SpringCmmnEngineConfiguration getCmmnEngineConfiguration() {
        return (SpringCmmnEngineConfiguration) cmmnEngineConfiguration;
    }

    public SpringCmmnEngineConfigurator setCmmnEngineConfiguration(SpringCmmnEngineConfiguration cmmnEngineConfiguration) {
        this.cmmnEngineConfiguration = cmmnEngineConfiguration;
        return this;
    }

}
