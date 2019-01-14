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
package com.eudemon.flow.cmmn.engine.configurator.impl.process;

import java.util.Map;

import com.eudemon.flow.cmmn.api.CallbackTypes;
import com.eudemon.flow.cmmn.engine.impl.process.ProcessInstanceService;
import com.eudemon.flow.engine.impl.cfg.ProcessEngineConfigurationImpl;
import com.eudemon.flow.engine.entity.BpmnEngineEntityConstants;
import com.eudemon.flow.engine.runtime.ProcessInstance;
import com.eudemon.flow.engine.runtime.ProcessInstanceBuilder;

/**
 * @author Joram Barrez
 */
public class DefaultProcessInstanceService implements ProcessInstanceService {

    private static final String DELETE_REASON = "deletedFromCmmnCase";

    protected ProcessEngineConfigurationImpl processEngineConfiguration;

    public DefaultProcessInstanceService(ProcessEngineConfigurationImpl processEngineConfiguration) {
        this.processEngineConfiguration = processEngineConfiguration;
    }

    @Override
    public String generateNewProcessInstanceId() {
        if (processEngineConfiguration.isUsePrefixId()) {
            return BpmnEngineEntityConstants.BPMN_ENGINE_ID_PREFIX + processEngineConfiguration.getIdGenerator().getNextId();
        } else {
            return processEngineConfiguration.getIdGenerator().getNextId();
        }
    }

    @Override
    public String startProcessInstanceByKey(String processDefinitionKey, String predefinedProcessInstanceId, String
                    tenantId, Map<String, Object> inParametersMap) {

        return startProcessInstanceByKey(processDefinitionKey, predefinedProcessInstanceId, null, tenantId, inParametersMap);
    }

    @Override
    public String startProcessInstanceByKey(String processDefinitionKey, String predefinedProcessInstanceId,
                    String planItemInstanceId, String tenantId, Map<String, Object> inParametersMap) {

        ProcessInstanceBuilder processInstanceBuilder = processEngineConfiguration.getRuntimeService().createProcessInstanceBuilder();
        processInstanceBuilder.processDefinitionKey(processDefinitionKey);
        if (tenantId != null) {
            processInstanceBuilder.tenantId(tenantId);
        }

        processInstanceBuilder.predefineProcessInstanceId(predefinedProcessInstanceId);

        if (planItemInstanceId != null) {
            processInstanceBuilder.callbackId(planItemInstanceId);
            processInstanceBuilder.callbackType(CallbackTypes.PLAN_ITEM_CHILD_PROCESS);
        }

        for (String target : inParametersMap.keySet()) {
            processInstanceBuilder.variable(target, inParametersMap.get(target));
        }

        ProcessInstance processInstance = processInstanceBuilder.start();
        return processInstance.getId();
    }

    @Override
    public void deleteProcessInstance(String processInstanceId) {
        processEngineConfiguration.getRuntimeService().deleteProcessInstance(processInstanceId, DELETE_REASON);
    }

    @Override
    public Map<String, Object> getVariables(String executionId){
       return processEngineConfiguration.getRuntimeService().getVariables(executionId);
    }

}
