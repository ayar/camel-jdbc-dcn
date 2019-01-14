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
package com.eudemon.flow.ui.task.service.runtime;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import com.eudemon.flow.app.repository.AppRepositoryService;
import com.eudemon.flow.app.model.AppDefinition;
import com.eudemon.flow.bpmn.model.BpmnModel;
import com.eudemon.flow.bpmn.model.FlowElement;
import com.eudemon.flow.bpmn.model.Process;
import com.eudemon.flow.bpmn.model.StartEvent;
import com.eudemon.flow.common.engine.api.FlowableObjectNotFoundException;
import com.eudemon.flow.editor.language.json.converter.util.CollectionUtils;
import com.eudemon.flow.engine.RepositoryService;
import com.eudemon.flow.engine.repository.Deployment;
import com.eudemon.flow.engine.repository.ProcessDefinition;
import com.eudemon.flow.engine.repository.ProcessDefinitionQuery;
import com.eudemon.flow.form.api.FormInfo;
import com.eudemon.flow.form.api.FormRepositoryService;
import com.eudemon.flow.ui.common.model.ResultListDataRepresentation;
import com.eudemon.flow.ui.common.security.SecurityUtils;
import com.eudemon.flow.ui.common.service.exception.NotFoundException;
import com.eudemon.flow.ui.task.model.runtime.ProcessDefinitionRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Tijs Rademakers
 */
@Service
@Transactional
public class FlowableProcessDefinitionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowableProcessDefinitionService.class);

    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    protected AppRepositoryService appRepositoryService;

    @Autowired
    protected FormRepositoryService formRepositoryService;

    @Autowired
    protected PermissionService permissionService;

    @Autowired
    protected ObjectMapper objectMapper;

    public FormInfo getProcessDefinitionStartForm(String processDefinitionId) {

        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(processDefinitionId);

        try {
            return getStartForm(processDefinition);

        } catch (FlowableObjectNotFoundException aonfe) {
            // Process definition does not exist
            throw new NotFoundException("No process definition found with the given id: " + processDefinitionId);
        }
    }

    public ResultListDataRepresentation getProcessDefinitions(Boolean latest, String appDefinitionKey) {

        ProcessDefinitionQuery definitionQuery = repositoryService.createProcessDefinitionQuery();

        if (appDefinitionKey != null) {
            AppDefinition appDefinition = appRepositoryService.createAppDefinitionQuery().appDefinitionKey(appDefinitionKey).latestVersion().singleResult();
            Deployment deployment = repositoryService.createDeploymentQuery().parentDeploymentId(appDefinition.getDeploymentId()).singleResult();

            if (deployment != null) {
                definitionQuery.deploymentId(deployment.getId());
            } else {
                return new ResultListDataRepresentation(new ArrayList<ProcessDefinitionRepresentation>());
            }

        } else {

            if (latest != null && latest) {
                definitionQuery.latestVersion();
            }
        }

        List<ProcessDefinition> definitions = definitionQuery.list();

        List<ProcessDefinition> startableDefinitions = new ArrayList<>();
        for (ProcessDefinition definition : definitions) {
            if (SecurityUtils.getCurrentUserObject() == null || permissionService.canStartProcess(SecurityUtils.getCurrentUserObject(), definition)) {
                startableDefinitions.add(definition);
            }
        }

        ResultListDataRepresentation result = new ResultListDataRepresentation(convertDefinitionList(startableDefinitions));
        return result;
    }

    protected FormInfo getStartForm(ProcessDefinition processDefinition) {
        FormInfo formInfo = null;
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        Process process = bpmnModel.getProcessById(processDefinition.getKey());
        FlowElement startElement = process.getInitialFlowElement();
        if (startElement instanceof StartEvent) {
            StartEvent startEvent = (StartEvent) startElement;
            if (StringUtils.isNotEmpty(startEvent.getFormKey())) {
                Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(processDefinition.getDeploymentId()).singleResult();
                formInfo = formRepositoryService.getFormModelByKeyAndParentDeploymentId(startEvent.getFormKey(),
                                deployment.getParentDeploymentId(), processDefinition.getTenantId());
            }
        }

        if (formInfo == null) {
            // Definition found, but no form attached
            throw new NotFoundException("Process definition does not have a form defined: " + processDefinition.getId());
        }

        return formInfo;
    }

    protected List<ProcessDefinitionRepresentation> convertDefinitionList(List<ProcessDefinition> definitions) {
        List<ProcessDefinitionRepresentation> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(definitions)) {
            for (ProcessDefinition processDefinition : definitions) {
                ProcessDefinitionRepresentation rep = new ProcessDefinitionRepresentation(processDefinition);
                result.add(rep);
            }
        }
        return result;
    }

}
