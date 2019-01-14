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

import java.util.HashMap;
import java.util.Map;

import com.eudemon.flow.bpmn.model.EventListener;
import com.eudemon.flow.bpmn.model.FlowableListener;
import com.eudemon.flow.bpmn.model.ImplementationType;
import com.eudemon.flow.common.engine.api.FlowableIllegalArgumentException;
import com.eudemon.flow.common.engine.api.delegate.event.FlowableEventListener;
import com.eudemon.flow.engine.delegate.CustomPropertiesResolver;
import com.eudemon.flow.engine.delegate.ExecutionListener;
import com.eudemon.flow.engine.delegate.TaskListener;
import com.eudemon.flow.engine.delegate.TransactionDependentTaskListener;
import com.eudemon.flow.engine.impl.bpmn.helper.BaseDelegateEventListener;
import com.eudemon.flow.engine.impl.bpmn.helper.ClassDelegateFactory;
import com.eudemon.flow.engine.impl.bpmn.helper.DefaultClassDelegateFactory;
import com.eudemon.flow.engine.impl.bpmn.helper.DelegateExpressionFlowableEventListener;
import com.eudemon.flow.engine.impl.bpmn.helper.DelegateFlowableEventListener;
import com.eudemon.flow.engine.impl.bpmn.helper.ErrorThrowingEventListener;
import com.eudemon.flow.engine.impl.bpmn.helper.MessageThrowingEventListener;
import com.eudemon.flow.engine.impl.bpmn.helper.SignalThrowingEventListener;
import com.eudemon.flow.engine.impl.bpmn.listener.DelegateExpressionCustomPropertiesResolver;
import com.eudemon.flow.engine.impl.bpmn.listener.DelegateExpressionExecutionListener;
import com.eudemon.flow.engine.impl.bpmn.listener.DelegateExpressionTaskListener;
import com.eudemon.flow.engine.impl.bpmn.listener.DelegateExpressionTransactionDependentExecutionListener;
import com.eudemon.flow.engine.impl.bpmn.listener.DelegateExpressionTransactionDependentTaskListener;
import com.eudemon.flow.engine.impl.bpmn.listener.ExpressionCustomPropertiesResolver;
import com.eudemon.flow.engine.impl.bpmn.listener.ExpressionExecutionListener;
import com.eudemon.flow.engine.impl.bpmn.listener.ExpressionTaskListener;
import com.eudemon.flow.engine.impl.cfg.ProcessEngineConfigurationImpl;
import com.eudemon.flow.engine.repository.ProcessDefinition;
import com.eudemon.flow.engine.runtime.Execution;
import com.eudemon.flow.engine.runtime.ProcessInstance;
import com.eudemon.flow.engine.task.Attachment;
import com.eudemon.flow.engine.task.Comment;
import com.eudemon.flow.identitylink.api.IdentityLink;
import com.eudemon.flow.job.api.Job;
import com.eudemon.flow.task.api.Task;

/**
 * Default implementation of the {@link ListenerFactory}. Used when no custom {@link ListenerFactory} is injected on the {@link ProcessEngineConfigurationImpl}.
 *
 * @author Joram Barrez
 */
public class DefaultListenerFactory extends AbstractBehaviorFactory implements ListenerFactory {
    private final ClassDelegateFactory classDelegateFactory;

    public DefaultListenerFactory(ClassDelegateFactory classDelegateFactory) {
        this.classDelegateFactory = classDelegateFactory;
    }

    public DefaultListenerFactory() {
        this(new DefaultClassDelegateFactory());
    }

    public static final Map<String, Class<?>> ENTITY_MAPPING = new HashMap<>();
    static {
        ENTITY_MAPPING.put("attachment", Attachment.class);
        ENTITY_MAPPING.put("comment", Comment.class);
        ENTITY_MAPPING.put("execution", Execution.class);
        ENTITY_MAPPING.put("identity-link", IdentityLink.class);
        ENTITY_MAPPING.put("job", Job.class);
        ENTITY_MAPPING.put("process-definition", ProcessDefinition.class);
        ENTITY_MAPPING.put("process-instance", ProcessInstance.class);
        ENTITY_MAPPING.put("task", Task.class);
    }

    @Override
    public TaskListener createClassDelegateTaskListener(FlowableListener listener) {
        return classDelegateFactory.create(listener.getImplementation(),
                createFieldDeclarations(listener.getFieldExtensions()));
    }

    @Override
    public TaskListener createExpressionTaskListener(FlowableListener listener) {
        return new ExpressionTaskListener(expressionManager.createExpression(listener.getImplementation()));
    }

    @Override
    public TaskListener createDelegateExpressionTaskListener(FlowableListener listener) {
        return new DelegateExpressionTaskListener(expressionManager.createExpression(listener.getImplementation()), createFieldDeclarations(listener.getFieldExtensions()));
    }

    @Override
    public TransactionDependentTaskListener createTransactionDependentDelegateExpressionTaskListener(FlowableListener listener) {
        return new DelegateExpressionTransactionDependentTaskListener(expressionManager.createExpression(listener.getImplementation()));
    }

    @Override
    public ExecutionListener createClassDelegateExecutionListener(FlowableListener listener) {
        return classDelegateFactory.create(listener.getImplementation(), createFieldDeclarations(listener.getFieldExtensions()));
    }

    @Override
    public ExecutionListener createExpressionExecutionListener(FlowableListener listener) {
        return new ExpressionExecutionListener(expressionManager.createExpression(listener.getImplementation()));
    }

    @Override
    public ExecutionListener createDelegateExpressionExecutionListener(FlowableListener listener) {
        return new DelegateExpressionExecutionListener(expressionManager.createExpression(listener.getImplementation()), createFieldDeclarations(listener.getFieldExtensions()));
    }

    @Override
    public DelegateExpressionTransactionDependentExecutionListener createTransactionDependentDelegateExpressionExecutionListener(FlowableListener listener) {
        return new DelegateExpressionTransactionDependentExecutionListener(expressionManager.createExpression(listener.getImplementation()));
    }

    @Override
    public FlowableEventListener createClassDelegateEventListener(EventListener eventListener) {
        return new DelegateFlowableEventListener(eventListener.getImplementation(), getEntityType(eventListener.getEntityType()));
    }

    @Override
    public FlowableEventListener createDelegateExpressionEventListener(EventListener eventListener) {
        return new DelegateExpressionFlowableEventListener(expressionManager.createExpression(eventListener.getImplementation()), getEntityType(eventListener.getEntityType()));
    }

    @Override
    public FlowableEventListener createEventThrowingEventListener(EventListener eventListener) {
        BaseDelegateEventListener result = null;
        if (ImplementationType.IMPLEMENTATION_TYPE_THROW_SIGNAL_EVENT.equals(eventListener.getImplementationType())) {
            result = new SignalThrowingEventListener();
            ((SignalThrowingEventListener) result).setSignalName(eventListener.getImplementation());
            ((SignalThrowingEventListener) result).setProcessInstanceScope(true);
        } else if (ImplementationType.IMPLEMENTATION_TYPE_THROW_GLOBAL_SIGNAL_EVENT.equals(eventListener.getImplementationType())) {
            result = new SignalThrowingEventListener();
            ((SignalThrowingEventListener) result).setSignalName(eventListener.getImplementation());
            ((SignalThrowingEventListener) result).setProcessInstanceScope(false);
        } else if (ImplementationType.IMPLEMENTATION_TYPE_THROW_MESSAGE_EVENT.equals(eventListener.getImplementationType())) {
            result = new MessageThrowingEventListener();
            ((MessageThrowingEventListener) result).setMessageName(eventListener.getImplementation());
        } else if (ImplementationType.IMPLEMENTATION_TYPE_THROW_ERROR_EVENT.equals(eventListener.getImplementationType())) {
            result = new ErrorThrowingEventListener();
            ((ErrorThrowingEventListener) result).setErrorCode(eventListener.getImplementation());
        }

        if (result == null) {
            throw new FlowableIllegalArgumentException("Cannot create an event-throwing event-listener, unknown implementation type: " + eventListener.getImplementationType());
        }

        result.setEntityClass(getEntityType(eventListener.getEntityType()));
        return result;
    }

    @Override
    public CustomPropertiesResolver createClassDelegateCustomPropertiesResolver(FlowableListener listener) {
        return classDelegateFactory.create(listener.getCustomPropertiesResolverImplementation(), null);
    }

    @Override
    public CustomPropertiesResolver createExpressionCustomPropertiesResolver(FlowableListener listener) {
        return new ExpressionCustomPropertiesResolver(expressionManager.createExpression(listener.getCustomPropertiesResolverImplementation()));
    }

    @Override
    public CustomPropertiesResolver createDelegateExpressionCustomPropertiesResolver(FlowableListener listener) {
        return new DelegateExpressionCustomPropertiesResolver(expressionManager.createExpression(listener.getCustomPropertiesResolverImplementation()));
    }

    /**
     * @param entityType
     *            the name of the entity
     * @return
     * @throws FlowableIllegalArgumentException
     *             when the given entity name
     */
    protected Class<?> getEntityType(String entityType) {
        if (entityType != null) {
            Class<?> entityClass = ENTITY_MAPPING.get(entityType.trim());
            if (entityClass == null) {
                throw new FlowableIllegalArgumentException("Unsupported entity-type for a FlowableEventListener: " + entityType);
            }
            return entityClass;
        }
        return null;
    }
}
