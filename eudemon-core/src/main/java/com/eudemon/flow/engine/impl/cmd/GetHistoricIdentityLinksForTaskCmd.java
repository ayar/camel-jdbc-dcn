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
import java.util.List;

import com.eudemon.flow.common.engine.api.FlowableIllegalArgumentException;
import com.eudemon.flow.common.engine.api.FlowableObjectNotFoundException;
import com.eudemon.flow.common.engine.impl.interceptor.Command;
import com.eudemon.flow.common.engine.impl.interceptor.CommandContext;
import com.eudemon.flow.engine.impl.util.CommandContextUtil;
import com.eudemon.flow.identitylink.api.history.HistoricIdentityLink;
import com.eudemon.flow.identitylink.service.HistoricIdentityLinkService;
import com.eudemon.flow.identitylink.api.IdentityLinkType;
import com.eudemon.flow.identitylink.api.HistoricIdentityLinkEntity;
import com.eudemon.flow.task.api.history.HistoricTaskInstance;
import com.eudemon.flow.task.api.HistoricTaskInstanceEntity;

/**
 * @author Frederik Heremans
 */
public class GetHistoricIdentityLinksForTaskCmd implements Command<List<HistoricIdentityLink>>, Serializable {

    private static final long serialVersionUID = 1L;
    protected String taskId;
    protected String processInstanceId;

    public GetHistoricIdentityLinksForTaskCmd(String taskId, String processInstanceId) {
        if (taskId == null && processInstanceId == null) {
            throw new FlowableIllegalArgumentException("taskId or processInstanceId is required");
        }
        this.taskId = taskId;
        this.processInstanceId = processInstanceId;
    }

    @Override
    public List<HistoricIdentityLink> execute(CommandContext commandContext) {
        if (taskId != null) {
            return getLinksForTask(commandContext);
        } else {
            return getLinksForProcessInstance(commandContext);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected List<HistoricIdentityLink> getLinksForTask(CommandContext commandContext) {
        HistoricTaskInstanceEntity task = CommandContextUtil.getHistoricTaskService().getHistoricTask(taskId);

        if (task == null) {
            throw new FlowableObjectNotFoundException("No historic task exists with the given id: " + taskId, HistoricTaskInstance.class);
        }

        HistoricIdentityLinkService historicIdentityLinkService = CommandContextUtil.getHistoricIdentityLinkService();
        List<HistoricIdentityLinkEntity> identityLinks = historicIdentityLinkService.findHistoricIdentityLinksByTaskId(taskId);

        HistoricIdentityLinkEntity assigneeIdentityLink = null;
        HistoricIdentityLinkEntity ownerIdentityLink = null;
        for (HistoricIdentityLinkEntity historicIdentityLink : identityLinks) {
            if (IdentityLinkType.ASSIGNEE.equals(historicIdentityLink.getType())) {
                assigneeIdentityLink = historicIdentityLink;

            } else if (IdentityLinkType.OWNER.equals(historicIdentityLink.getType())) {
                ownerIdentityLink = historicIdentityLink;
            }
        }

        // Similar to GetIdentityLinksForTask, return assignee and owner as identity link
        if (task.getAssignee() != null && assigneeIdentityLink == null) {
            HistoricIdentityLinkEntity identityLink = historicIdentityLinkService.createHistoricIdentityLink();
            identityLink.setUserId(task.getAssignee());
            identityLink.setTaskId(task.getId());
            identityLink.setType(IdentityLinkType.ASSIGNEE);
            identityLinks.add(identityLink);
        }

        if (task.getOwner() != null && ownerIdentityLink == null) {
            HistoricIdentityLinkEntity identityLink = historicIdentityLinkService.createHistoricIdentityLink();
            identityLink.setTaskId(task.getId());
            identityLink.setUserId(task.getOwner());
            identityLink.setType(IdentityLinkType.OWNER);
            identityLinks.add(identityLink);
        }

        return (List) identityLinks;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected List<HistoricIdentityLink> getLinksForProcessInstance(CommandContext commandContext) {
        return (List) CommandContextUtil.getHistoricIdentityLinkService().findHistoricIdentityLinksByProcessInstanceId(processInstanceId);
    }

}
