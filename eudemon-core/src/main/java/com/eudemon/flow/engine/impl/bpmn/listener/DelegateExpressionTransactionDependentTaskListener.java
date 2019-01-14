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
package com.eudemon.flow.engine.impl.bpmn.listener;

import java.util.Map;

import com.eudemon.flow.bpmn.model.Task;
import com.eudemon.flow.common.engine.api.FlowableIllegalArgumentException;
import com.eudemon.flow.common.engine.api.delegate.Expression;
import com.eudemon.flow.engine.delegate.TransactionDependentTaskListener;
import com.eudemon.flow.variable.service.impl.el.NoExecutionVariableScope;

/**
 * @author Yvo Swillens
 */
public class DelegateExpressionTransactionDependentTaskListener implements TransactionDependentTaskListener {

    protected Expression expression;

    public DelegateExpressionTransactionDependentTaskListener(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void notify(String processInstanceId, String executionId, Task task, Map<String, Object> executionVariables, Map<String, Object> customPropertiesMap) {
        NoExecutionVariableScope scope = new NoExecutionVariableScope();

        Object delegate = expression.getValue(scope);

        if (delegate instanceof TransactionDependentTaskListener) {
            ((TransactionDependentTaskListener) delegate).notify(processInstanceId, executionId, task, executionVariables, customPropertiesMap);
        } else {
            throw new FlowableIllegalArgumentException("Delegate expression " + expression + " did not resolve to an implementation of " + TransactionDependentTaskListener.class);
        }

    }

    /**
     * returns the expression text for this task listener. Comes in handy if you want to check which listeners you already have.
     */
    public String getExpressionText() {
        return expression.getExpressionText();
    }

}