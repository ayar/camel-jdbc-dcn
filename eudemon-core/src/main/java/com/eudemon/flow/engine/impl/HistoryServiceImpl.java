/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eudemon.flow.engine.impl;

import java.util.List;

import com.eudemon.flow.common.engine.impl.service.CommonEngineServiceImpl;
import com.eudemon.flow.engine.HistoryService;
import com.eudemon.flow.engine.history.HistoricActivityInstanceQuery;
import com.eudemon.flow.engine.history.HistoricDetailQuery;
import com.eudemon.flow.engine.history.HistoricProcessInstanceQuery;
import com.eudemon.flow.engine.history.NativeHistoricActivityInstanceQuery;
import com.eudemon.flow.engine.history.NativeHistoricDetailQuery;
import com.eudemon.flow.engine.history.NativeHistoricProcessInstanceQuery;
import com.eudemon.flow.engine.history.ProcessInstanceHistoryLogQuery;
import com.eudemon.flow.engine.impl.cfg.ProcessEngineConfigurationImpl;
import com.eudemon.flow.engine.impl.cmd.DeleteHistoricProcessInstanceCmd;
import com.eudemon.flow.engine.impl.cmd.DeleteHistoricTaskInstanceCmd;
import com.eudemon.flow.engine.impl.cmd.GetHistoricEntityLinkChildrenForProcessInstanceCmd;
import com.eudemon.flow.engine.impl.cmd.GetHistoricIdentityLinksForTaskCmd;
import com.eudemon.flow.entitylink.api.history.HistoricEntityLink;
import com.eudemon.flow.identitylink.api.history.HistoricIdentityLink;
import com.eudemon.flow.task.api.history.HistoricTaskInstanceQuery;
import com.eudemon.flow.task.service.history.NativeHistoricTaskInstanceQuery;
import com.eudemon.flow.task.service.impl.HistoricTaskInstanceQueryImpl;
import com.eudemon.flow.task.service.impl.NativeHistoricTaskInstanceQueryImpl;
import com.eudemon.flow.variable.api.history.HistoricVariableInstanceQuery;
import com.eudemon.flow.variable.api.history.NativeHistoricVariableInstanceQuery;
import com.eudemon.flow.variable.service.impl.HistoricVariableInstanceQueryImpl;
import com.eudemon.flow.variable.service.impl.NativeHistoricVariableInstanceQueryImpl;

/**
 * @author Tom Baeyens
 * @author Bernd Ruecker (camunda)
 * @author Christian Stettler
 */
public class HistoryServiceImpl extends CommonEngineServiceImpl<ProcessEngineConfigurationImpl> implements HistoryService {

    public HistoryServiceImpl(ProcessEngineConfigurationImpl processEngineConfiguration) {
        super(processEngineConfiguration);
    }

    @Override
    public HistoricProcessInstanceQuery createHistoricProcessInstanceQuery() {
        return new HistoricProcessInstanceQueryImpl(commandExecutor);
    }

    @Override
    public HistoricActivityInstanceQuery createHistoricActivityInstanceQuery() {
        return new HistoricActivityInstanceQueryImpl(commandExecutor);
    }

    @Override
    public HistoricTaskInstanceQuery createHistoricTaskInstanceQuery() {
        return new HistoricTaskInstanceQueryImpl(commandExecutor, configuration.getDatabaseType());
    }

    @Override
    public HistoricDetailQuery createHistoricDetailQuery() {
        return new HistoricDetailQueryImpl(commandExecutor);
    }

    @Override
    public NativeHistoricDetailQuery createNativeHistoricDetailQuery() {
        return new NativeHistoricDetailQueryImpl(commandExecutor);
    }

    @Override
    public HistoricVariableInstanceQuery createHistoricVariableInstanceQuery() {
        return new HistoricVariableInstanceQueryImpl(commandExecutor);
    }

    @Override
    public NativeHistoricVariableInstanceQuery createNativeHistoricVariableInstanceQuery() {
        return new NativeHistoricVariableInstanceQueryImpl(commandExecutor);
    }

    @Override
    public void deleteHistoricTaskInstance(String taskId) {
        commandExecutor.execute(new DeleteHistoricTaskInstanceCmd(taskId));
    }

    @Override
    public void deleteHistoricProcessInstance(String processInstanceId) {
        commandExecutor.execute(new DeleteHistoricProcessInstanceCmd(processInstanceId));
    }

    @Override
    public NativeHistoricProcessInstanceQuery createNativeHistoricProcessInstanceQuery() {
        return new NativeHistoricProcessInstanceQueryImpl(commandExecutor);
    }

    @Override
    public NativeHistoricTaskInstanceQuery createNativeHistoricTaskInstanceQuery() {
        return new NativeHistoricTaskInstanceQueryImpl(commandExecutor);
    }

    @Override
    public NativeHistoricActivityInstanceQuery createNativeHistoricActivityInstanceQuery() {
        return new NativeHistoricActivityInstanceQueryImpl(commandExecutor);
    }

    @Override
    public List<HistoricIdentityLink> getHistoricIdentityLinksForProcessInstance(String processInstanceId) {
        return commandExecutor.execute(new GetHistoricIdentityLinksForTaskCmd(null, processInstanceId));
    }

    @Override
    public List<HistoricIdentityLink> getHistoricIdentityLinksForTask(String taskId) {
        return commandExecutor.execute(new GetHistoricIdentityLinksForTaskCmd(taskId, null));
    }

    @Override
    public List<HistoricEntityLink> getHistoricEntityLinkChildrenForProcessInstance(String processInstanceId) {
        return commandExecutor.execute(new GetHistoricEntityLinkChildrenForProcessInstanceCmd(processInstanceId));
    }

    @Override
    public ProcessInstanceHistoryLogQuery createProcessInstanceHistoryLogQuery(String processInstanceId) {
        return new ProcessInstanceHistoryLogQueryImpl(commandExecutor, processInstanceId);
    }

}
