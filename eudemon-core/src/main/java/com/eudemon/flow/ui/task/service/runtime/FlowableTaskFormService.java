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

import java.util.Map;

import com.eudemon.flow.cmmn.api.CmmnTaskService;
import com.eudemon.flow.engine.HistoryService;
import com.eudemon.flow.engine.RepositoryService;
import com.eudemon.flow.engine.TaskService;
import com.eudemon.flow.form.api.FormInfo;
import com.eudemon.flow.form.api.FormRepositoryService;
import com.eudemon.flow.form.api.FormService;
import com.eudemon.flow.idm.api.User;
import com.eudemon.flow.task.api.Task;
import com.eudemon.flow.task.api.history.HistoricTaskInstance;
import com.eudemon.flow.ui.common.security.SecurityUtils;
import com.eudemon.flow.ui.common.service.exception.NotFoundException;
import com.eudemon.flow.ui.common.service.exception.NotPermittedException;
import com.eudemon.flow.ui.task.model.runtime.CompleteFormRepresentation;
import com.eudemon.flow.ui.task.model.runtime.SaveFormRepresentation;
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
public class FlowableTaskFormService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowableTaskFormService.class);

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected CmmnTaskService cmmnTaskService;

    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected FormRepositoryService formRepositoryService;

    @Autowired
    protected FormService formService;

    @Autowired
    protected PermissionService permissionService;

    @Autowired
    protected ObjectMapper objectMapper;

    public FormInfo getTaskForm(String taskId) {
        HistoricTaskInstance task = permissionService.validateReadPermissionOnTask(SecurityUtils.getCurrentUserObject(), taskId);
        if (task.getProcessInstanceId() != null) {
            return taskService.getTaskFormModel(task.getId());
        } else {
            return cmmnTaskService.getTaskFormModel(task.getId());
        }
    }

    public void saveTaskForm(String taskId, SaveFormRepresentation saveFormRepresentation) {

        // Get the form definition
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        if (task == null) {
            throw new NotFoundException("Task not found with id: " + taskId);
        }

        checkCurrentUserCanModifyTask(task);

        FormInfo formInfo = formRepositoryService.getFormModelById(saveFormRepresentation.getFormId());
        Map<String, Object> formVariables = formService.getVariablesFromFormSubmission(formInfo, saveFormRepresentation.getValues(), null);

        if (task.getProcessInstanceId() != null) {
            formService.saveFormInstanceByFormDefinitionId(formVariables, saveFormRepresentation.getFormId(), taskId,
                            task.getProcessInstanceId(), task.getProcessDefinitionId());

        } else {
            formService.saveFormInstanceWithScopeId(formVariables, saveFormRepresentation.getFormId(), taskId,
                            task.getScopeId(), task.getScopeType(), task.getScopeDefinitionId());
        }

    }

    public void completeTaskForm(String taskId, CompleteFormRepresentation completeTaskFormRepresentation) {

        // Get the form definition
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        if (task == null) {
            throw new NotFoundException("Task not found with id: " + taskId);
        }

        checkCurrentUserCanModifyTask(task);

        if (task.getProcessInstanceId() != null) {
            taskService.completeTaskWithForm(taskId, completeTaskFormRepresentation.getFormId(),
                    completeTaskFormRepresentation.getOutcome(), completeTaskFormRepresentation.getValues());
        } else {
            cmmnTaskService.completeTaskWithForm(taskId, completeTaskFormRepresentation.getFormId(), completeTaskFormRepresentation.getOutcome(),
                            completeTaskFormRepresentation.getValues());
        }
    }

    protected void checkCurrentUserCanModifyTask(Task task) {
        User currentUser = SecurityUtils.getCurrentUserObject();
        if (!permissionService.isTaskOwnerOrAssignee(currentUser, task.getId())) {
            if (!permissionService.validateIfUserIsInitiatorAndCanCompleteTask(currentUser, task)) {
                throw new NotPermittedException();
            }
        }
    }
}
