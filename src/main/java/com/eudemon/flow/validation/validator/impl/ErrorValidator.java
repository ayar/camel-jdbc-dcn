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
package com.eudemon.flow.validation.validator.impl;

import java.util.List;

import com.eudemon.flow.bpmn.model.BpmnModel;
import com.eudemon.flow.validation.ValidationError;
import com.eudemon.flow.validation.validator.Problems;
import com.eudemon.flow.validation.validator.ValidatorImpl;

/**
 * @author jbarrez
 */
public class ErrorValidator extends ValidatorImpl {

    @Override
    public void validate(BpmnModel bpmnModel, List<ValidationError> errors) {
        if (bpmnModel.getErrors() != null) {
            for (String errorRef : bpmnModel.getErrors().keySet()) {
                String errorCode = bpmnModel.getErrors().get(errorRef);
                if ("".equals(errorCode)) {
                    addError(errors, Problems.ERROR_MISSING_ERROR_CODE, null, errorRef, "Invalid error code: empty errorCode");
                }
            }
        }
    }

}
