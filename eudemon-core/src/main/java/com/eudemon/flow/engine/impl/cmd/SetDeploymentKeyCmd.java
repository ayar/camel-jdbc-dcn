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
package com.eudemon.flow.engine.impl.cmd;

import com.eudemon.flow.common.engine.api.FlowableException;
import com.eudemon.flow.common.engine.api.FlowableIllegalArgumentException;
import com.eudemon.flow.common.engine.api.FlowableObjectNotFoundException;
import com.eudemon.flow.common.engine.api.delegate.event.FlowableEngineEventType;
import com.eudemon.flow.common.engine.impl.interceptor.Command;
import com.eudemon.flow.common.engine.impl.interceptor.CommandContext;
import com.eudemon.flow.engine.delegate.event.impl.FlowableEventBuilder;
import com.eudemon.flow.engine.entity.DeploymentEntity;
import com.eudemon.flow.engine.impl.util.CommandContextUtil;
import com.eudemon.flow.engine.impl.util.Flowable5Util;
import com.eudemon.flow.engine.repository.Deployment;

/**
 * @author Tijs Rademakers
 */
public class SetDeploymentKeyCmd implements Command<Void> {

    protected String deploymentId;
    protected String key;

    public SetDeploymentKeyCmd(String deploymentId, String key) {
        this.deploymentId = deploymentId;
        this.key = key;
    }

    @Override
    public Void execute(CommandContext commandContext) {

        if (deploymentId == null) {
            throw new FlowableIllegalArgumentException("Deployment id is null");
        }

        DeploymentEntity deployment = CommandContextUtil.getDeploymentEntityManager(commandContext).findById(deploymentId);

        if (deployment == null) {
            throw new FlowableObjectNotFoundException("No deployment found for id = '" + deploymentId + "'", Deployment.class);
        }

        if (Flowable5Util.isFlowable5Deployment(deployment, commandContext)) {
            throw new FlowableException("Not supported for version 5 deployments");
        }

        // Update category
        deployment.setKey(key);

        if (CommandContextUtil.getProcessEngineConfiguration(commandContext).getEventDispatcher().isEnabled()) {
            CommandContextUtil.getProcessEngineConfiguration(commandContext).getEventDispatcher().dispatchEvent(FlowableEventBuilder.createEntityEvent(FlowableEngineEventType.ENTITY_UPDATED, deployment));
        }

        return null;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}