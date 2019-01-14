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

package com.eudemon.flow.engine.impl.bpmn.behavior;

import com.eudemon.flow.common.engine.api.delegate.Expression;
import com.eudemon.flow.engine.delegate.DelegateExecution;
import com.eudemon.flow.engine.delegate.ExecutionListener;
import com.eudemon.flow.engine.delegate.JavaDelegate;
import com.eudemon.flow.engine.impl.bpmn.helper.SkipExpressionUtil;
import com.eudemon.flow.engine.impl.delegate.ActivityBehavior;
import com.eudemon.flow.engine.impl.delegate.TriggerableActivityBehavior;
import com.eudemon.flow.engine.impl.delegate.invocation.JavaDelegateInvocation;
import com.eudemon.flow.engine.impl.util.CommandContextUtil;

/**
 * @author Tom Baeyens
 */
public class ServiceTaskJavaDelegateActivityBehavior extends TaskActivityBehavior implements ActivityBehavior, ExecutionListener {

    private static final long serialVersionUID = 1L;

    protected JavaDelegate javaDelegate;
    protected Expression skipExpression;
    protected boolean triggerable;

    protected ServiceTaskJavaDelegateActivityBehavior() {
    }

    public ServiceTaskJavaDelegateActivityBehavior(JavaDelegate javaDelegate, boolean triggerable, Expression skipExpression) {
        this.javaDelegate = javaDelegate;
        this.triggerable = triggerable;
        this.skipExpression = skipExpression;
    }

    @Override
    public void trigger(DelegateExecution execution, String signalName, Object signalData) {
        if (triggerable && javaDelegate instanceof TriggerableActivityBehavior) {
            ((TriggerableActivityBehavior) javaDelegate).trigger(execution, signalName, signalData);
            leave(execution);
        }
    }

    @Override
    public void execute(DelegateExecution execution) {
        boolean isSkipExpressionEnabled = SkipExpressionUtil.isSkipExpressionEnabled(execution, skipExpression);
        if (!isSkipExpressionEnabled || (isSkipExpressionEnabled && !SkipExpressionUtil.shouldSkipFlowElement(execution, skipExpression))) {
            CommandContextUtil.getProcessEngineConfiguration().getDelegateInterceptor()
                .handleInvocation(new JavaDelegateInvocation(javaDelegate, execution));
        }

        if (!triggerable) {
            leave(execution);
        }
    }

    @Override
    public void notify(DelegateExecution execution) {
        execute(execution);
    }
}
