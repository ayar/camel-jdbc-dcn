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
package com.eudemon.flow.cmmn.engine.configurator.impl.deployer;

import java.util.Map;

import com.eudemon.flow.cmmn.api.CmmnRepositoryService;
import com.eudemon.flow.cmmn.api.repository.CmmnDeploymentBuilder;
import com.eudemon.flow.cmmn.engine.impl.util.CommandContextUtil;
import com.eudemon.flow.common.engine.api.repository.EngineDeployment;
import com.eudemon.flow.common.engine.api.repository.EngineResource;
import com.eudemon.flow.common.engine.impl.EngineDeployer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Joram Barrez
 */
public class CmmnDeployer implements EngineDeployer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmmnDeployer.class);

    @Override
    public void deploy(EngineDeployment deployment, Map<String, Object> deploymentSettings) {
        if (!deployment.isNew()) {
            return;
        }

        LOGGER.debug("CmmnDeployer: processing deployment {}", deployment.getName());

        CmmnDeploymentBuilder cmmnDeploymentBuilder = null;
        Map<String, EngineResource> resources = deployment.getResources();
        for (String resourceName : resources.keySet()) {
            if (com.eudemon.flow.cmmn.engine.impl.deployer.CmmnDeployer.isCmmnResource(resourceName)) {
                LOGGER.info("CmmnDeployer: processing resource {}", resourceName);
                if (cmmnDeploymentBuilder == null) {
                    CmmnRepositoryService cmmnRepositoryService = CommandContextUtil.getCmmnRepositoryService();
                    cmmnDeploymentBuilder = cmmnRepositoryService.createDeployment().name(deployment.getName());
                }
                cmmnDeploymentBuilder.addBytes(resourceName, resources.get(resourceName).getBytes());
            }
        }

        if (cmmnDeploymentBuilder != null) {
            cmmnDeploymentBuilder.parentDeploymentId(deployment.getId());
            cmmnDeploymentBuilder.key(deployment.getKey());
            if (deployment.getTenantId() != null && deployment.getTenantId().length() > 0) {
                cmmnDeploymentBuilder.tenantId(deployment.getTenantId());
            }
            cmmnDeploymentBuilder.deploy();
        }
    }

}
