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
import com.eudemon.flow.cmmn.api.CmmnRepositoryService;
import com.eudemon.flow.cmmn.api.repository.CaseDefinition;
import com.eudemon.flow.cmmn.api.repository.CaseDefinitionQuery;
import com.eudemon.flow.cmmn.api.repository.CmmnDeployment;
import com.eudemon.flow.cmmn.model.Case;
import com.eudemon.flow.cmmn.model.CmmnModel;
import com.eudemon.flow.common.engine.api.FlowableObjectNotFoundException;
import com.eudemon.flow.editor.language.json.converter.util.CollectionUtils;
import com.eudemon.flow.form.api.FormInfo;
import com.eudemon.flow.form.api.FormRepositoryService;
import com.eudemon.flow.ui.common.model.ResultListDataRepresentation;
import com.eudemon.flow.ui.common.service.exception.NotFoundException;
import com.eudemon.flow.ui.task.model.runtime.CaseDefinitionRepresentation;
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
public class FlowableCaseDefinitionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowableCaseDefinitionService.class);

    @Autowired
    protected CmmnRepositoryService cmmnRepositoryService;

    @Autowired
    protected AppRepositoryService appRepositoryService;

    @Autowired
    protected PermissionService permissionService;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected FormRepositoryService formRepositoryService;

    public ResultListDataRepresentation getCaseDefinitions(Boolean latest, String appDefinitionKey) {

        CaseDefinitionQuery definitionQuery = cmmnRepositoryService.createCaseDefinitionQuery();

        if (appDefinitionKey != null) {
            AppDefinition appDefinition = appRepositoryService.createAppDefinitionQuery().appDefinitionKey(appDefinitionKey).latestVersion().singleResult();
            CmmnDeployment deployment = cmmnRepositoryService.createDeploymentQuery().parentDeploymentId(appDefinition.getDeploymentId()).singleResult();

            if (deployment != null) {
                definitionQuery.deploymentId(deployment.getId());
            } else {
                return new ResultListDataRepresentation(new ArrayList<CaseDefinitionRepresentation>());
            }

        } else {

            if (latest != null && latest) {
                definitionQuery.latestVersion();
            }
        }

        List<CaseDefinition> definitions = definitionQuery.list();

        ResultListDataRepresentation result = new ResultListDataRepresentation(convertDefinitionList(definitions));
        return result;
    }

    public FormInfo getCaseDefinitionStartForm(String caseDefinitionId) {

        CaseDefinition caseDefinition = cmmnRepositoryService.getCaseDefinition(caseDefinitionId);

        try {
            return getStartForm(caseDefinition);

        } catch (FlowableObjectNotFoundException aonfe) {
            throw new NotFoundException("No case definition found with the given id: " + caseDefinitionId);
        }
    }

    protected List<CaseDefinitionRepresentation> convertDefinitionList(List<CaseDefinition> definitions) {
        List<CaseDefinitionRepresentation> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(definitions)) {
            for (CaseDefinition caseDefinition : definitions) {
                CaseDefinitionRepresentation rep = new CaseDefinitionRepresentation(caseDefinition);
                result.add(rep);
            }
        }
        return result;
    }

    protected FormInfo getStartForm(CaseDefinition caseDefinition) {
        CmmnModel cmmnModel = this.cmmnRepositoryService.getCmmnModel(caseDefinition.getId());
        List<Case> cases = cmmnModel.getCases();
        if (cases == null || cases.size() != 1) {
            throw new FlowableObjectNotFoundException("Case definition " + caseDefinition.getId()+ " start form was not found.");
        }

        Case caze = cases.get(0);
        if (caze == null || caze.getPlanModel() == null || StringUtils.isEmpty(caze.getPlanModel().getFormKey())) {
            throw new FlowableObjectNotFoundException("Case from case definition " + caseDefinition.getId() + " does not contain any start form.");
        }

        String formKey = caze.getPlanModel().getFormKey();
        return formRepositoryService.getFormModelByKey(formKey);
    }

}
