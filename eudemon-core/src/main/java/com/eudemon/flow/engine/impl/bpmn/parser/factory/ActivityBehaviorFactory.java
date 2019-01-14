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
package com.eudemon.flow.engine.impl.bpmn.parser.factory;

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
import com.eudemon.flow.engine.impl.bpmn.parser.BpmnParse;
import com.eudemon.flow.engine.impl.bpmn.parser.BpmnParser;
import com.eudemon.flow.engine.impl.cfg.ProcessEngineConfigurationImpl;
import com.eudemon.flow.engine.impl.delegate.ActivityBehavior;

/**
 * Factory class used by the {@link BpmnParser} and {@link BpmnParse} to instantiate the behaviour classes. For example when parsing an exclusive gateway, this factory will be requested to create a
 * new {@link ActivityBehavior} that will be set on the {@link ActivityImpl} of that step of the process and will implement the spec-compliant behavior of the exclusive gateway.
 *
 * You can provide your own implementation of this class. This way, you can give different execution semantics to a standard bpmn xml construct. Eg. you could tweak the exclusive gateway to do
 * something completely different if you would want that. Creating your own {@link ActivityBehaviorFactory} is only advisable if you want to change the default behavior of any BPMN default construct.
 * And even then, think twice, because it won't be spec compliant bpmn anymore.
 *
 * Note that you can always express any custom step as a service task with a class delegation.
 *
 * The easiest and advisable way to implement your own {@link ActivityBehaviorFactory} is to extend the {@link DefaultActivityBehaviorFactory} class and override the method specific to the
 * {@link ActivityBehavior} you want to change.
 *
 * An instance of this interface can be injected in the {@link ProcessEngineConfigurationImpl} and its subclasses.
 *
 * @author Joram Barrez
 */
public interface ActivityBehaviorFactory {

    public abstract NoneStartEventActivityBehavior createNoneStartEventActivityBehavior(StartEvent startEvent);

    public abstract TaskActivityBehavior createTaskActivityBehavior(Task task);

    public abstract ManualTaskActivityBehavior createManualTaskActivityBehavior(ManualTask manualTask);

    public abstract ReceiveTaskActivityBehavior createReceiveTaskActivityBehavior(ReceiveTask receiveTask);

    public abstract UserTaskActivityBehavior createUserTaskActivityBehavior(UserTask userTask);

    public abstract ClassDelegate createClassDelegateServiceTask(ServiceTask serviceTask);

    public abstract ServiceTaskDelegateExpressionActivityBehavior createServiceTaskDelegateExpressionActivityBehavior(ServiceTask serviceTask);

    public abstract ServiceTaskExpressionActivityBehavior createServiceTaskExpressionActivityBehavior(ServiceTask serviceTask);

    public abstract WebServiceActivityBehavior createWebServiceActivityBehavior(ServiceTask serviceTask, BpmnModel bpmnModel);

    public abstract WebServiceActivityBehavior createWebServiceActivityBehavior(SendTask sendTask, BpmnModel bpmnModel);

    public abstract MailActivityBehavior createMailActivityBehavior(ServiceTask serviceTask);

    public abstract MailActivityBehavior createMailActivityBehavior(SendTask sendTask);

    // We do not want a hard dependency on the Mule module, hence we return
    // ActivityBehavior and instantiate the delegate instance using a string instead of the Class itself.
    public abstract ActivityBehavior createMuleActivityBehavior(ServiceTask serviceTask);

    public abstract ActivityBehavior createMuleActivityBehavior(SendTask sendTask);

    public abstract ActivityBehavior createCamelActivityBehavior(ServiceTask serviceTask);

    public abstract ActivityBehavior createCamelActivityBehavior(SendTask sendTask);

    public abstract ActivityBehavior createDmnActivityBehavior(ServiceTask serviceTask);

    public abstract ActivityBehavior createDmnActivityBehavior(SendTask sendTask);

    public abstract ActivityBehavior createHttpActivityBehavior(ServiceTask serviceTask);

    public abstract ShellActivityBehavior createShellActivityBehavior(ServiceTask serviceTask);

    public abstract ActivityBehavior createBusinessRuleTaskActivityBehavior(BusinessRuleTask businessRuleTask);

    public abstract ScriptTaskActivityBehavior createScriptTaskActivityBehavior(ScriptTask scriptTask);

    public abstract ExclusiveGatewayActivityBehavior createExclusiveGatewayActivityBehavior(ExclusiveGateway exclusiveGateway);

    public abstract ParallelGatewayActivityBehavior createParallelGatewayActivityBehavior(ParallelGateway parallelGateway);

    public abstract InclusiveGatewayActivityBehavior createInclusiveGatewayActivityBehavior(InclusiveGateway inclusiveGateway);

    public abstract EventBasedGatewayActivityBehavior createEventBasedGatewayActivityBehavior(EventGateway eventGateway);

    public abstract SequentialMultiInstanceBehavior createSequentialMultiInstanceBehavior(Activity activity, AbstractBpmnActivityBehavior innerActivityBehavior);

    public abstract ParallelMultiInstanceBehavior createParallelMultiInstanceBehavior(Activity activity, AbstractBpmnActivityBehavior innerActivityBehavior);

    public abstract SubProcessActivityBehavior createSubprocessActivityBehavior(SubProcess subProcess);

    public abstract EventSubProcessActivityBehavior createEventSubprocessActivityBehavior(EventSubProcess eventSubProcess);

    public abstract EventSubProcessErrorStartEventActivityBehavior createEventSubProcessErrorStartEventActivityBehavior(StartEvent startEvent);

    public abstract EventSubProcessMessageStartEventActivityBehavior createEventSubProcessMessageStartEventActivityBehavior(StartEvent startEvent, MessageEventDefinition messageEventDefinition);

    public abstract EventSubProcessSignalStartEventActivityBehavior createEventSubProcessSignalStartEventActivityBehavior(StartEvent startEvent, SignalEventDefinition signalEventDefinition, Signal signal);

    public abstract EventSubProcessTimerStartEventActivityBehavior createEventSubProcessTimerStartEventActivityBehavior(StartEvent startEvent, TimerEventDefinition timerEventDefinition);

    public abstract AdhocSubProcessActivityBehavior createAdhocSubprocessActivityBehavior(SubProcess subProcess);

    public abstract CallActivityBehavior createCallActivityBehavior(CallActivity callActivity);

    public abstract TransactionActivityBehavior createTransactionActivityBehavior(Transaction transaction);

    public abstract IntermediateCatchEventActivityBehavior createIntermediateCatchEventActivityBehavior(IntermediateCatchEvent intermediateCatchEvent);

    public abstract IntermediateCatchMessageEventActivityBehavior createIntermediateCatchMessageEventActivityBehavior(IntermediateCatchEvent intermediateCatchEvent,
                                                                                                                      MessageEventDefinition messageEventDefinition);

    public abstract IntermediateCatchTimerEventActivityBehavior createIntermediateCatchTimerEventActivityBehavior(IntermediateCatchEvent intermediateCatchEvent, TimerEventDefinition timerEventDefinition);

    public abstract IntermediateCatchSignalEventActivityBehavior createIntermediateCatchSignalEventActivityBehavior(IntermediateCatchEvent intermediateCatchEvent,
                                                                                                                    SignalEventDefinition signalEventDefinition, Signal signal);

    public abstract IntermediateThrowNoneEventActivityBehavior createIntermediateThrowNoneEventActivityBehavior(ThrowEvent throwEvent);

    public abstract IntermediateThrowSignalEventActivityBehavior createIntermediateThrowSignalEventActivityBehavior(ThrowEvent throwEvent, SignalEventDefinition signalEventDefinition, Signal signal);

    public abstract IntermediateThrowCompensationEventActivityBehavior createIntermediateThrowCompensationEventActivityBehavior(ThrowEvent throwEvent, CompensateEventDefinition compensateEventDefinition);

    public abstract NoneEndEventActivityBehavior createNoneEndEventActivityBehavior(EndEvent endEvent);

    public abstract ErrorEndEventActivityBehavior createErrorEndEventActivityBehavior(EndEvent endEvent, ErrorEventDefinition errorEventDefinition);

    public abstract CancelEndEventActivityBehavior createCancelEndEventActivityBehavior(EndEvent endEvent);

    public abstract TerminateEndEventActivityBehavior createTerminateEndEventActivityBehavior(EndEvent endEvent);

    public abstract BoundaryEventActivityBehavior createBoundaryEventActivityBehavior(BoundaryEvent boundaryEvent, boolean interrupting);

    public abstract BoundaryCancelEventActivityBehavior createBoundaryCancelEventActivityBehavior(CancelEventDefinition cancelEventDefinition);

    public abstract BoundaryTimerEventActivityBehavior createBoundaryTimerEventActivityBehavior(BoundaryEvent boundaryEvent, TimerEventDefinition timerEventDefinition, boolean interrupting);

    public abstract BoundarySignalEventActivityBehavior createBoundarySignalEventActivityBehavior(BoundaryEvent boundaryEvent, SignalEventDefinition signalEventDefinition, Signal signal, boolean interrupting);

    public abstract BoundaryMessageEventActivityBehavior createBoundaryMessageEventActivityBehavior(BoundaryEvent boundaryEvent, MessageEventDefinition messageEventDefinition, boolean interrupting);

    public abstract BoundaryCompensateEventActivityBehavior createBoundaryCompensateEventActivityBehavior(BoundaryEvent boundaryEvent, CompensateEventDefinition compensateEventDefinition, boolean interrupting);
}
