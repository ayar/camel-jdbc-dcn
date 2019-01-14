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
package com.eudemon.flow.engine.configurator.impl.deployer;

import java.util.Map;

import com.eudemon.flow.common.engine.api.repository.EngineDeployment;
import com.eudemon.flow.common.engine.api.repository.EngineResource;
import com.eudemon.flow.engine.RepositoryService;
import com.eudemon.flow.engine.impl.bpmn.deployer.ResourceNameUtil;
import com.eudemon.flow.engine.impl.util.CommandContextUtil;
import com.eudemon.flow.engine.repository.DeploymentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Joram Barrez
 */
public class BpmnDeployer implements EngineDeployer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BpmnDeployer.class);

    @Override
    public void deploy(EngineDeployment deployment, Map<String, Object> deploymentSettings) {
        if (!deployment.isNew()) {
            return;
        }

        LOGGER.debug("BpmnDeployer: processing deployment {}", deployment.getName());

        DeploymentBuilder bpmnDeploymentBuilder = null;
        Map<String, EngineResource> resources = deployment.getResources();
        for (String resourceName : resources.keySet()) {
            if (isBpmnResource(resourceName)) {
                LOGGER.info("BpmnDeployer: processing resource {}", resourceName);
                if (bpmnDeploymentBuilder == null) {
                    RepositoryService repositoryService = CommandContextUtil.getProcessEngineConfiguration().getRepositoryService();
                    bpmnDeploymentBuilder = repositoryService.createDeployment().name(deployment.getName());
                }
                bpmnDeploymentBuilder.addBytes(resourceName, resources.get(resourceName).getBytes());
            }
        }

        if (bpmnDeploymentBuilder != null) {
            bpmnDeploymentBuilder.parentDeploymentId(deployment.getId());
            bpmnDeploymentBuilder.key(deployment.getKey());
            if (deployment.getTenantId() != null && deployment.getTenantId().length() > 0) {
                bpmnDeploymentBuilder.tenantId(deployment.getTenantId());
            }
            bpmnDeploymentBuilder.deploy();
        }
    }

    protected boolean isBpmnResource(String resourceName) {
        for (String suffix : ResourceNameUtil.BPMN_RESOURCE_SUFFIXES) {
            if (resourceName.endsWith(suffix)) {
                return true;
            }
        }

        return false;
    }
}
