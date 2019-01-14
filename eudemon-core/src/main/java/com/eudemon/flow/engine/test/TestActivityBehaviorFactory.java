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
package com.eudemon.flow.engine.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.eudemon.flow.bpmn.model.Activity;
import com.eudemon.flow.bpmn.model.BoundaryEvent;
import com.eudemon.flow.bpmn.model.BpmnModel;
import com.eudemon.flow.bpmn.model.BusinessRuleTask;
import com.eudemon.flow.bpmn.model.CallActivity;
import com.eudemon.flow.bpmn.model.CancelEventDefinition;
import com.eudemon.flow.bpmn.model.CompensateEventDefinition;
import com.eudemon.flow.bpmn.model.EndEvent;
import com.eudemon.flow.bpmn.model.ErrorEventDefinition;
import com.eudemon.flow.bpmn.model.EventGateway;
import com.eudemon.flow.bpmn.model.EventSubProcess;
import com.eudemon.flow.bpmn.model.ExclusiveGateway;
import com.eudemon.flow.bpmn.model.InclusiveGateway;
import com.eudemon.flow.bpmn.model.IntermediateCatchEvent;
import com.eudemon.flow.bpmn.model.ManualTask;
import com.eudemon.flow.bpmn.model.MessageEventDefinition;
import com.eudemon.flow.bpmn.model.ParallelGateway;
import com.eudemon.flow.bpmn.model.ReceiveTask;
import com.eudemon.flow.bpmn.model.ScriptTask;
import com.eudemon.flow.bpmn.model.SendTask;
import com.eudemon.flow.bpmn.model.ServiceTask;
import com.eudemon.flow.bpmn.model.Signal;
import com.eudemon.flow.bpmn.model.SignalEventDefinition;
import com.eudemon.flow.bpmn.model.StartEvent;
import com.eudemon.flow.bpmn.model.SubProcess;
import com.eudemon.flow.bpmn.model.Task;
import com.eudemon.flow.bpmn.model.ThrowEvent;
import com.eudemon.flow.bpmn.model.TimerEventDefinition;
import com.eudemon.flow.bpmn.model.Transaction;
import com.eudemon.flow.bpmn.model.UserTask;
import com.eudemon.flow.common.engine.api.delegate.Expression;
import com.eudemon.flow.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.AdhocSubProcessActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.BoundaryCancelEventActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.BoundaryCompensateEventActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.BoundaryEventActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.BoundaryMessageEventActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.BoundarySignalEventActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.BoundaryTimerEventActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.CallActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.CancelEndEventActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.ErrorEndEventActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.EventBasedGatewayActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.EventSubProcessActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.EventSubProcessErrorStartEventActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.EventSubProcessMessageStartEventActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.EventSubProcessSignalStartEventActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.EventSubProcessTimerStartEventActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.ExclusiveGatewayActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.InclusiveGatewayActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.IntermediateCatchEventActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.IntermediateCatchMessageEventActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.IntermediateCatchSignalEventActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.IntermediateCatchTimerEventActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.IntermediateThrowCompensationEventActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.IntermediateThrowNoneEventActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.IntermediateThrowSignalEventActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.MailActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.ManualTaskActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.NoneEndEventActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.NoneStartEventActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.ParallelGatewayActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.ReceiveTaskActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.ScriptTaskActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.ServiceTaskDelegateExpressionActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.ServiceTaskExpressionActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.ShellActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.SubProcessActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.TaskActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.TerminateEndEventActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.TransactionActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.behavior.WebServiceActivityBehavior;
import com.eudemon.flow.engine.impl.bpmn.helper.ClassDelegate;
import com.eudemon.flow.engine.impl.bpmn.parser.FieldDeclaration;
import com.eudemon.flow.engine.impl.bpmn.parser.factory.AbstractBehaviorFactory;
import com.eudemon.flow.engine.impl.bpmn.parser.factory.ActivityBehaviorFactory;
import com.eudemon.flow.engine.impl.delegate.ActivityBehavior;
import com.eudemon.flow.engine.impl.el.FixedValue;
import com.eudemon.flow.engine.impl.test.NoOpServiceTask;

/**
 * @author Joram Barrez
 */
public class TestActivityBehaviorFactory extends AbstractBehaviorFactory implements ActivityBehaviorFactory {

    /**
     * The ActivityBehaviorFactory that is constructed when the process engine was created This class delegates to this instance, unless some mocking has been defined.
     */
    protected ActivityBehaviorFactory wrappedActivityBehaviorFactory;

    protected boolean allServiceTasksNoOp;
    protected Map<String, String> mockedClassDelegatesMapping = new HashMap<>();
    protected Map<String, String> mockedClassTaskIdDelegatesMapping = new HashMap<>();
    protected Set<String> noOpServiceTaskIds = new HashSet<>();
    protected Set<String> noOpServiceTaskClassNames = new HashSet<>();

    public TestActivityBehaviorFactory() {

    }

    public TestActivityBehaviorFactory(ActivityBehaviorFactory wrappedActivityBehaviorFactory) {
        this.wrappedActivityBehaviorFactory = wrappedActivityBehaviorFactory;
    }

    public ActivityBehaviorFactory getWrappedActivityBehaviorFactory() {
        return wrappedActivityBehaviorFactory;
    }

    public void setWrappedActivityBehaviorFactory(ActivityBehaviorFactory wrappedActivityBehaviorFactory) {
        this.wrappedActivityBehaviorFactory = wrappedActivityBehaviorFactory;
    }

    @Override
    public NoneStartEventActivityBehavior createNoneStartEventActivityBehavior(StartEvent startEvent) {
        return wrappedActivityBehaviorFactory.createNoneStartEventActivityBehavior(startEvent);
    }

    @Override
    public TaskActivityBehavior createTaskActivityBehavior(Task task) {
        return wrappedActivityBehaviorFactory.createTaskActivityBehavior(task);
    }

    @Override
    public ManualTaskActivityBehavior createManualTaskActivityBehavior(ManualTask manualTask) {
        return wrappedActivityBehaviorFactory.createManualTaskActivityBehavior(manualTask);
    }

    @Override
    public ReceiveTaskActivityBehavior createReceiveTaskActivityBehavior(ReceiveTask receiveTask) {
        return wrappedActivityBehaviorFactory.createReceiveTaskActivityBehavior(receiveTask);
    }

    @Override
    public UserTaskActivityBehavior createUserTaskActivityBehavior(UserTask userTask) {
        return wrappedActivityBehaviorFactory.createUserTaskActivityBehavior(userTask);
    }

    @Override
    public ClassDelegate createClassDelegateServiceTask(ServiceTask serviceTask) {

        if (allServiceTasksNoOp || noOpServiceTaskIds.contains(serviceTask.getId()) || noOpServiceTaskClassNames.contains(serviceTask.getImplementation())) {

            return createNoOpServiceTask(serviceTask);

        } else if (serviceTask.getImplementation() != null && mockedClassDelegatesMapping.containsKey(serviceTask.getImplementation())) {

            return new ClassDelegate(mockedClassDelegatesMapping.get(serviceTask.getImplementation()), createFieldDeclarations(serviceTask.getFieldExtensions()));

        } else if (serviceTask.getId() != null && mockedClassTaskIdDelegatesMapping.containsKey(serviceTask.getId())) {
            return new ClassDelegate(mockedClassTaskIdDelegatesMapping.get(serviceTask.getId()), createFieldDeclarations(serviceTask.getFieldExtensions()));
        }

        return wrappedActivityBehaviorFactory.createClassDelegateServiceTask(serviceTask);
    }

    private ClassDelegate createNoOpServiceTask(ServiceTask serviceTask) {
        List<FieldDeclaration> fieldDeclarations = new ArrayList<>();
        fieldDeclarations.add(new FieldDeclaration("name", Expression.class.getName(), new FixedValue(serviceTask.getImplementation())));
        return new ClassDelegate(NoOpServiceTask.class, fieldDeclarations);
    }

    @Override
    public ServiceTaskDelegateExpressionActivityBehavior createServiceTaskDelegateExpressionActivityBehavior(ServiceTask serviceTask) {
        return wrappedActivityBehaviorFactory.createServiceTaskDelegateExpressionActivityBehavior(serviceTask);
    }

    @Override
    public ServiceTaskExpressionActivityBehavior createServiceTaskExpressionActivityBehavior(ServiceTask serviceTask) {
        return wrappedActivityBehaviorFactory.createServiceTaskExpressionActivityBehavior(serviceTask);
    }

    @Override
    public WebServiceActivityBehavior createWebServiceActivityBehavior(ServiceTask serviceTask, BpmnModel bpmnModel) {
        return wrappedActivityBehaviorFactory.createWebServiceActivityBehavior(serviceTask, bpmnModel);
    }

    @Override
    public WebServiceActivityBehavior createWebServiceActivityBehavior(SendTask sendTask, BpmnModel bpmnModel) {
        return wrappedActivityBehaviorFactory.createWebServiceActivityBehavior(sendTask, bpmnModel);
    }

    @Override
    public MailActivityBehavior createMailActivityBehavior(ServiceTask serviceTask) {
        return wrappedActivityBehaviorFactory.createMailActivityBehavior(serviceTask);
    }

    @Override
    public MailActivityBehavior createMailActivityBehavior(SendTask sendTask) {
        return wrappedActivityBehaviorFactory.createMailActivityBehavior(sendTask);
    }

    @Override
    public ActivityBehavior createDmnActivityBehavior(ServiceTask serviceTask) {
        return wrappedActivityBehaviorFactory.createDmnActivityBehavior(serviceTask);
    }

    @Override
    public ActivityBehavior createDmnActivityBehavior(SendTask sendTask) {
        return wrappedActivityBehaviorFactory.createDmnActivityBehavior(sendTask);
    }

    @Override
    public ActivityBehavior createMuleActivityBehavior(ServiceTask serviceTask) {
        return wrappedActivityBehaviorFactory.createMuleActivityBehavior(serviceTask);
    }

    @Override
    public ActivityBehavior createMuleActivityBehavior(SendTask sendTask) {
        return wrappedActivityBehaviorFactory.createMuleActivityBehavior(sendTask);
    }

    @Override
    public ActivityBehavior createCamelActivityBehavior(ServiceTask serviceTask) {
        return wrappedActivityBehaviorFactory.createCamelActivityBehavior(serviceTask);
    }

    @Override
    public ActivityBehavior createCamelActivityBehavior(SendTask sendTask) {
        return wrappedActivityBehaviorFactory.createCamelActivityBehavior(sendTask);
    }

    @Override
    public ShellActivityBehavior createShellActivityBehavior(ServiceTask serviceTask) {
        return wrappedActivityBehaviorFactory.createShellActivityBehavior(serviceTask);
    }

    @Override
    public ActivityBehavior createHttpActivityBehavior(ServiceTask serviceTask) {
        return wrappedActivityBehaviorFactory.createHttpActivityBehavior(serviceTask);
    }

    @Override
    public ActivityBehavior createBusinessRuleTaskActivityBehavior(BusinessRuleTask businessRuleTask) {
        return wrappedActivityBehaviorFactory.createBusinessRuleTaskActivityBehavior(businessRuleTask);
    }

    @Override
    public ScriptTaskActivityBehavior createScriptTaskActivityBehavior(ScriptTask scriptTask) {
        return wrappedActivityBehaviorFactory.createScriptTaskActivityBehavior(scriptTask);
    }

    @Override
    public ExclusiveGatewayActivityBehavior createExclusiveGatewayActivityBehavior(ExclusiveGateway exclusiveGateway) {
        return wrappedActivityBehaviorFactory.createExclusiveGatewayActivityBehavior(exclusiveGateway);
    }

    @Override
    public ParallelGatewayActivityBehavior createParallelGatewayActivityBehavior(ParallelGateway parallelGateway) {
        return wrappedActivityBehaviorFactory.createParallelGatewayActivityBehavior(parallelGateway);
    }

    @Override
    public InclusiveGatewayActivityBehavior createInclusiveGatewayActivityBehavior(InclusiveGateway inclusiveGateway) {
        return wrappedActivityBehaviorFactory.createInclusiveGatewayActivityBehavior(inclusiveGateway);
    }

    @Override
    public EventBasedGatewayActivityBehavior createEventBasedGatewayActivityBehavior(EventGateway eventGateway) {
        return wrappedActivityBehaviorFactory.createEventBasedGatewayActivityBehavior(eventGateway);
    }

    @Override
    public SequentialMultiInstanceBehavior createSequentialMultiInstanceBehavior(Activity activity, AbstractBpmnActivityBehavior innerActivityBehavior) {
        return wrappedActivityBehaviorFactory.createSequentialMultiInstanceBehavior(activity, innerActivityBehavior);
    }

    @Override
    public ParallelMultiInstanceBehavior createParallelMultiInstanceBehavior(Activity activity, AbstractBpmnActivityBehavior innerActivityBehavior) {
        return wrappedActivityBehaviorFactory.createParallelMultiInstanceBehavior(activity, innerActivityBehavior);
    }

    @Override
    public SubProcessActivityBehavior createSubprocessActivityBehavior(SubProcess subProcess) {
        return wrappedActivityBehaviorFactory.createSubprocessActivityBehavior(subProcess);
    }

    @Override
    public EventSubProcessActivityBehavior createEventSubprocessActivityBehavior(EventSubProcess eventSubProcess) {
        return wrappedActivityBehaviorFactory.createEventSubprocessActivityBehavior(eventSubProcess);
    }

    @Override
    public EventSubProcessErrorStartEventActivityBehavior createEventSubProcessErrorStartEventActivityBehavior(StartEvent startEvent) {
        return wrappedActivityBehaviorFactory.createEventSubProcessErrorStartEventActivityBehavior(startEvent);
    }

    @Override
    public EventSubProcessMessageStartEventActivityBehavior createEventSubProcessMessageStartEventActivityBehavior(StartEvent startEvent, MessageEventDefinition messageEventDefinition) {
        return wrappedActivityBehaviorFactory.createEventSubProcessMessageStartEventActivityBehavior(startEvent, messageEventDefinition);
    }

    @Override
    public EventSubProcessSignalStartEventActivityBehavior createEventSubProcessSignalStartEventActivityBehavior(StartEvent startEvent, SignalEventDefinition signalEventDefinition, Signal signal) {
        return wrappedActivityBehaviorFactory.createEventSubProcessSignalStartEventActivityBehavior(startEvent, signalEventDefinition, signal);
    }

    @Override
    public EventSubProcessTimerStartEventActivityBehavior createEventSubProcessTimerStartEventActivityBehavior(StartEvent startEvent, TimerEventDefinition timerEventDefinition) {
        return wrappedActivityBehaviorFactory.createEventSubProcessTimerStartEventActivityBehavior(startEvent, timerEventDefinition);
    }

    @Override
    public AdhocSubProcessActivityBehavior createAdhocSubprocessActivityBehavior(SubProcess subProcess) {
        return wrappedActivityBehaviorFactory.createAdhocSubprocessActivityBehavior(subProcess);
    }

    @Override
    public CallActivityBehavior createCallActivityBehavior(CallActivity callActivity) {
        return wrappedActivityBehaviorFactory.createCallActivityBehavior(callActivity);
    }

    @Override
    public TransactionActivityBehavior createTransactionActivityBehavior(Transaction transaction) {
        return wrappedActivityBehaviorFactory.createTransactionActivityBehavior(transaction);
    }

    @Override
    public IntermediateCatchEventActivityBehavior createIntermediateCatchEventActivityBehavior(IntermediateCatchEvent intermediateCatchEvent) {
        return wrappedActivityBehaviorFactory.createIntermediateCatchEventActivityBehavior(intermediateCatchEvent);
    }

    @Override
    public IntermediateCatchMessageEventActivityBehavior createIntermediateCatchMessageEventActivityBehavior(IntermediateCatchEvent intermediateCatchEvent, MessageEventDefinition messageEventDefinition) {

        return wrappedActivityBehaviorFactory.createIntermediateCatchMessageEventActivityBehavior(intermediateCatchEvent, messageEventDefinition);
    }

    @Override
    public IntermediateCatchTimerEventActivityBehavior createIntermediateCatchTimerEventActivityBehavior(IntermediateCatchEvent intermediateCatchEvent, TimerEventDefinition timerEventDefinition) {
        return wrappedActivityBehaviorFactory.createIntermediateCatchTimerEventActivityBehavior(intermediateCatchEvent, timerEventDefinition);
    }

    @Override
    public IntermediateCatchSignalEventActivityBehavior createIntermediateCatchSignalEventActivityBehavior(IntermediateCatchEvent intermediateCatchEvent, SignalEventDefinition signalEventDefinition,
            Signal signal) {

        return wrappedActivityBehaviorFactory.createIntermediateCatchSignalEventActivityBehavior(intermediateCatchEvent, signalEventDefinition, signal);
    }

    @Override
    public IntermediateThrowNoneEventActivityBehavior createIntermediateThrowNoneEventActivityBehavior(ThrowEvent throwEvent) {
        return wrappedActivityBehaviorFactory.createIntermediateThrowNoneEventActivityBehavior(throwEvent);
    }

    @Override
    public IntermediateThrowSignalEventActivityBehavior createIntermediateThrowSignalEventActivityBehavior(ThrowEvent throwEvent, SignalEventDefinition signalEventDefinition, Signal signal) {

        return wrappedActivityBehaviorFactory.createIntermediateThrowSignalEventActivityBehavior(throwEvent, signalEventDefinition, signal);
    }

    @Override
    public IntermediateThrowCompensationEventActivityBehavior createIntermediateThrowCompensationEventActivityBehavior(ThrowEvent throwEvent, CompensateEventDefinition compensateEventDefinition) {
        return wrappedActivityBehaviorFactory.createIntermediateThrowCompensationEventActivityBehavior(throwEvent, compensateEventDefinition);
    }

    @Override
    public NoneEndEventActivityBehavior createNoneEndEventActivityBehavior(EndEvent endEvent) {
        return wrappedActivityBehaviorFactory.createNoneEndEventActivityBehavior(endEvent);
    }

    @Override
    public ErrorEndEventActivityBehavior createErrorEndEventActivityBehavior(EndEvent endEvent, ErrorEventDefinition errorEventDefinition) {
        return wrappedActivityBehaviorFactory.createErrorEndEventActivityBehavior(endEvent, errorEventDefinition);
    }

    @Override
    public CancelEndEventActivityBehavior createCancelEndEventActivityBehavior(EndEvent endEvent) {
        return wrappedActivityBehaviorFactory.createCancelEndEventActivityBehavior(endEvent);
    }

    @Override
    public TerminateEndEventActivityBehavior createTerminateEndEventActivityBehavior(EndEvent endEvent) {
        return wrappedActivityBehaviorFactory.createTerminateEndEventActivityBehavior(endEvent);
    }

    @Override
    public BoundaryEventActivityBehavior createBoundaryEventActivityBehavior(BoundaryEvent boundaryEvent, boolean interrupting) {
        return wrappedActivityBehaviorFactory.createBoundaryEventActivityBehavior(boundaryEvent, interrupting);
    }

    @Override
    public BoundaryCancelEventActivityBehavior createBoundaryCancelEventActivityBehavior(CancelEventDefinition cancelEventDefinition) {
        return wrappedActivityBehaviorFactory.createBoundaryCancelEventActivityBehavior(cancelEventDefinition);
    }

    @Override
    public BoundaryTimerEventActivityBehavior createBoundaryTimerEventActivityBehavior(BoundaryEvent boundaryEvent, TimerEventDefinition timerEventDefinition, boolean interrupting) {
        return wrappedActivityBehaviorFactory.createBoundaryTimerEventActivityBehavior(boundaryEvent, timerEventDefinition, interrupting);
    }

    @Override
    public BoundarySignalEventActivityBehavior createBoundarySignalEventActivityBehavior(BoundaryEvent boundaryEvent, SignalEventDefinition signalEventDefinition, Signal signal, boolean interrupting) {
        return wrappedActivityBehaviorFactory.createBoundarySignalEventActivityBehavior(boundaryEvent, signalEventDefinition, signal, interrupting);
    }

    @Override
    public BoundaryMessageEventActivityBehavior createBoundaryMessageEventActivityBehavior(BoundaryEvent boundaryEvent, MessageEventDefinition messageEventDefinition, boolean interrupting) {
        return wrappedActivityBehaviorFactory.createBoundaryMessageEventActivityBehavior(boundaryEvent, messageEventDefinition, interrupting);
    }

    @Override
    public BoundaryCompensateEventActivityBehavior createBoundaryCompensateEventActivityBehavior(BoundaryEvent boundaryEvent, CompensateEventDefinition compensateEventDefinition, boolean interrupting) {
        return wrappedActivityBehaviorFactory.createBoundaryCompensateEventActivityBehavior(boundaryEvent, compensateEventDefinition, interrupting);
    }

    // Mock support //////////////////////////////////////////////////////

    public void addClassDelegateMock(String originalClassFqn, Class<?> mockClass) {
        mockedClassDelegatesMapping.put(originalClassFqn, mockClass.getName());
    }

    public void addClassDelegateMock(String originalClassFqn, String mockedClassFqn) {
        mockedClassDelegatesMapping.put(originalClassFqn, mockedClassFqn);
    }

    public void addClassDelegateMockByTaskId(String serviceTaskId, Class<?> mockedClass) {
        addClassDelegateMockByTaskId(serviceTaskId, mockedClass.getName());
    }

    public void addClassDelegateMockByTaskId(String serviceTaskId, String mockedClassFqn) {
        mockedClassTaskIdDelegatesMapping.put(serviceTaskId, mockedClassFqn);
    }

    public void addNoOpServiceTaskById(String id) {
        noOpServiceTaskIds.add(id);
    }

    public void addNoOpServiceTaskByClassName(String className) {
        noOpServiceTaskClassNames.add(className);
    }

    public void setAllServiceTasksNoOp() {
        allServiceTasksNoOp = true;
    }

    public void reset() {
        this.mockedClassDelegatesMapping.clear();

        this.noOpServiceTaskIds.clear();
        this.noOpServiceTaskClassNames.clear();

        allServiceTasksNoOp = false;
        NoOpServiceTask.reset();
    }

}
