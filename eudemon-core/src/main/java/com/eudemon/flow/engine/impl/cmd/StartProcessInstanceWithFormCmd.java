/* Licensed under the Apache License, Version 2.0 (the "License");
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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import com.eudemon.flow.bpmn.model.BpmnModel;
import com.eudemon.flow.bpmn.model.FlowElement;
import com.eudemon.flow.bpmn.model.Process;
import com.eudemon.flow.bpmn.model.StartEvent;
import com.eudemon.flow.bpmn.model.ValuedDataObject;
import com.eudemon.flow.common.engine.api.FlowableObjectNotFoundException;
import com.eudemon.flow.common.engine.impl.interceptor.Command;
import com.eudemon.flow.common.engine.impl.interceptor.CommandContext;
import com.eudemon.flow.engine.impl.cfg.ProcessEngineConfigurationImpl;
import com.eudemon.flow.engine.impl.persistence.deploy.DeploymentManager;
import com.eudemon.flow.engine.impl.util.CommandContextUtil;
import com.eudemon.flow.engine.impl.util.ProcessDefinitionUtil;
import com.eudemon.flow.engine.impl.util.ProcessInstanceHelper;
import com.eudemon.flow.engine.repository.ProcessDefinition;
import com.eudemon.flow.engine.runtime.ProcessInstance;
import com.eudemon.flow.form.api.FormFieldHandler;
import com.eudemon.flow.form.api.FormInfo;
import com.eudemon.flow.form.api.FormRepositoryService;
import com.eudemon.flow.form.api.FormService;

/**
 * @author Tom Baeyens
 * @author Joram Barrez
 */
public class StartProcessInstanceWithFormCmd implements Command<ProcessInstance>, Serializable {

    private static final long serialVersionUID = 1L;
    protected String processDefinitionId;
    protected String outcome;
    protected Map<String, Object> variables;
    protected String processInstanceName;

    public StartProcessInstanceWithFormCmd(String processDefinitionId, String outcome, Map<String, Object> variables, String processInstanceName) {
        this.processDefinitionId = processDefinitionId;
        this.outcome = outcome;
        this.variables = variables;
        this.processInstanceName = processInstanceName;
    }

    @Override
    public ProcessInstance execute(CommandContext commandContext) {
        ProcessEngineConfigurationImpl processEngineConfiguration = CommandContextUtil.getProcessEngineConfiguration(commandContext);
        DeploymentManager deploymentCache = processEngineConfiguration.getDeploymentManager();

        // Find the process definition
        ProcessDefinition processDefinition = deploymentCache.findDeployedProcessDefinitionById(processDefinitionId);
        if (processDefinition == null) {
            throw new FlowableObjectNotFoundException("No process definition found for id = '" + processDefinitionId + "'", ProcessDefinition.class);
        }

        FormInfo formInfo = null;
        Map<String, Object> formVariables = null;
        FormService formService = CommandContextUtil.getFormService();

        if (variables != null || outcome != null) {
            BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(processDefinition.getId());
            Process process = bpmnModel.getProcessById(processDefinition.getKey());
            FlowElement startElement = process.getInitialFlowElement();
            if (startElement instanceof StartEvent) {
                StartEvent startEvent = (StartEvent) startElement;
                if (StringUtils.isNotEmpty(startEvent.getFormKey())) {
                    FormRepositoryService formRepositoryService = CommandContextUtil.getFormRepositoryService();
                    formInfo = formRepositoryService.getFormModelByKey(startEvent.getFormKey());
                    if (formInfo != null) {
                        formVariables = formService.getVariablesFromFormSubmission(formInfo, variables, outcome);
                    }
                }
            }
        }

        ProcessInstance processInstance = createAndStartProcessInstance(processDefinition, processInstanceName,
                formVariables, commandContext);

        if (formInfo != null) {
            formService.createFormInstance(formVariables, formInfo, null, processInstance.getId(), processInstance.getProcessDefinitionId());
            FormFieldHandler formFieldHandler = CommandContextUtil.getProcessEngineConfiguration(commandContext).getFormFieldHandler();
            formFieldHandler.handleFormFieldsOnSubmit(formInfo, null, processInstance.getId(), null, null, variables);
        }

        return processInstance;
    }

    protected ProcessInstance createAndStartProcessInstance(ProcessDefinition processDefinition, String processInstanceName,
            Map<String, Object> variables, CommandContext commandContext) {

        ProcessInstanceHelper processInstanceHelper = CommandContextUtil.getProcessEngineConfiguration(commandContext).getProcessInstanceHelper();
        return processInstanceHelper.createAndStartProcessInstance(processDefinition, null, processInstanceName, variables, null);
    }

    protected Map<String, Object> processDataObjects(Collection<ValuedDataObject> dataObjects) {
        Map<String, Object> variablesMap = new HashMap<>();
        // convert data objects to process variables
        if (dataObjects != null) {
            for (ValuedDataObject dataObject : dataObjects) {
                variablesMap.put(dataObject.getName(), dataObject.getValue());
            }
        }
        return variablesMap;
    }
}
