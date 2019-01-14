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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import com.eudemon.flow.bpmn.model.BpmnModel;
import com.eudemon.flow.bpmn.model.ExtensionElement;
import com.eudemon.flow.bpmn.model.FlowElement;
import com.eudemon.flow.bpmn.model.UserTask;
import com.eudemon.flow.cmmn.api.CmmnRepositoryService;
import com.eudemon.flow.cmmn.api.CmmnTaskService;
import com.eudemon.flow.editor.language.json.converter.util.CollectionUtils;
import com.eudemon.flow.engine.HistoryService;
import com.eudemon.flow.engine.RepositoryService;
import com.eudemon.flow.engine.TaskService;
import com.eudemon.flow.engine.history.HistoricProcessInstance;
import com.eudemon.flow.identitylink.api.history.HistoricIdentityLink;
import com.eudemon.flow.identitylink.api.IdentityLinkType;
import com.eudemon.flow.idm.api.User;
import com.eudemon.flow.task.api.TaskInfo;
import com.eudemon.flow.ui.common.model.RemoteGroup;
import com.eudemon.flow.ui.common.service.idm.RemoteIdmService;
import com.eudemon.flow.ui.task.model.runtime.TaskRepresentation;
import com.eudemon.flow.ui.task.service.api.UserCache;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class FlowableAbstractTaskService {

    @Autowired
    protected RemoteIdmService remoteIdmService;

    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    protected CmmnRepositoryService cmmnRepositoryService;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected CmmnTaskService cmmnTaskService;

    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected UserCache userCache;

    @Autowired
    protected PermissionService permissionService;

    public void fillPermissionInformation(TaskRepresentation taskRepresentation, TaskInfo task, User currentUser) {
        verifyProcessInstanceStartUser(taskRepresentation, task);

        List<HistoricIdentityLink> taskIdentityLinks = historyService.getHistoricIdentityLinksForTask(task.getId());
        verifyCandidateGroups(taskRepresentation, currentUser, taskIdentityLinks);
        verifyCandidateUsers(taskRepresentation, currentUser, taskIdentityLinks);
    }

    protected void verifyProcessInstanceStartUser(TaskRepresentation taskRepresentation, TaskInfo task) {
        if (task.getProcessInstanceId() != null) {
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            if (historicProcessInstance != null && StringUtils.isNotEmpty(historicProcessInstance.getStartUserId())) {
                taskRepresentation.setProcessInstanceStartUserId(historicProcessInstance.getStartUserId());
                BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
                FlowElement flowElement = bpmnModel.getFlowElement(task.getTaskDefinitionKey());
                if (flowElement instanceof UserTask) {
                    UserTask userTask = (UserTask) flowElement;
                    List<ExtensionElement> extensionElements = userTask.getExtensionElements().get("initiator-can-complete");
                    if (CollectionUtils.isNotEmpty(extensionElements)) {
                        String value = extensionElements.get(0).getElementText();
                        if (StringUtils.isNotEmpty(value)) {
                            taskRepresentation.setInitiatorCanCompleteTask(Boolean.valueOf(value));
                        }
                    }
                }
            }
        }
    }

    protected void verifyCandidateGroups(TaskRepresentation taskRepresentation, User currentUser, List<HistoricIdentityLink> taskIdentityLinks) {
        List<RemoteGroup> userGroups = remoteIdmService.getUser(currentUser.getId()).getGroups();
        taskRepresentation.setMemberOfCandidateGroup(userGroupsMatchTaskCandidateGroups(userGroups, taskIdentityLinks));
    }

    protected boolean userGroupsMatchTaskCandidateGroups(List<RemoteGroup> userGroups, List<HistoricIdentityLink> taskIdentityLinks) {
        for (RemoteGroup group : userGroups) {
            for (HistoricIdentityLink identityLink : taskIdentityLinks) {
                if (identityLink.getGroupId() != null
                        && identityLink.getType().equals(IdentityLinkType.CANDIDATE)
                        && group.getId().equals(identityLink.getGroupId())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void verifyCandidateUsers(TaskRepresentation taskRepresentation, User currentUser, List<HistoricIdentityLink> taskIdentityLinks) {
        taskRepresentation.setMemberOfCandidateUsers(currentUserMatchesTaskCandidateUsers(currentUser, taskIdentityLinks));
    }

    protected boolean currentUserMatchesTaskCandidateUsers(User currentUser, List<HistoricIdentityLink> taskIdentityLinks) {
        for (HistoricIdentityLink identityLink : taskIdentityLinks) {
            if (identityLink.getUserId() != null
                    && identityLink.getType().equals(IdentityLinkType.CANDIDATE)
                    && identityLink.getUserId().equals(currentUser.getId())) {
                return true;
            }
        }
        return false;
    }

}
