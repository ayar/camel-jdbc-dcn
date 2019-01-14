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

package com.eudemon.flow.engine.impl.form;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import com.eudemon.flow.bpmn.model.FormProperty;
import com.eudemon.flow.engine.form.StartFormData;
import com.eudemon.flow.engine.entity.DeploymentEntity;
import com.eudemon.flow.engine.entity.ExecutionEntity;
import com.eudemon.flow.engine.entity.ProcessDefinitionEntity;
import com.eudemon.flow.engine.repository.ProcessDefinition;

/**
 * @author Tom Baeyens
 */
public class DefaultStartFormHandler extends DefaultFormHandler implements StartFormHandler {

    @Override
    public void parseConfiguration(List<FormProperty> formProperties, String formKey, DeploymentEntity deployment, ProcessDefinition processDefinition) {
        super.parseConfiguration(formProperties, formKey, deployment, processDefinition);
        if (StringUtils.isNotEmpty(formKey)) {
            ((ProcessDefinitionEntity) processDefinition).setStartFormKey(true);
        }
    }

    @Override
    public StartFormData createStartFormData(ProcessDefinition processDefinition) {
        StartFormDataImpl startFormData = new StartFormDataImpl();
        if (formKey != null) {
            startFormData.setFormKey(formKey.getExpressionText());
        }
        startFormData.setDeploymentId(deploymentId);
        startFormData.setProcessDefinition(processDefinition);
        initializeFormProperties(startFormData, null);
        return startFormData;
    }

    public ExecutionEntity submitStartFormData(ExecutionEntity processInstance, Map<String, String> properties) {
        submitFormProperties(properties, processInstance);
        return processInstance;
    }
}
