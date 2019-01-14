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
package com.eudemon.flow.dmn.engine;

import com.eudemon.flow.common.engine.impl.FlowableVersions;
import com.eudemon.flow.dmn.api.DmnHistoryService;
import com.eudemon.flow.dmn.api.DmnManagementService;
import com.eudemon.flow.dmn.api.DmnRepositoryService;
import com.eudemon.flow.dmn.api.DmnRuleService;

public interface DmnEngine {


    /**
     * The name as specified in 'dmn-engine-name' in the flowable.dmn.cfg.xml configuration file. The default name for a dmn engine is 'default
     */
    String getName();

    void close();

    DmnManagementService getDmnManagementService();

    DmnRepositoryService getDmnRepositoryService();

    DmnRuleService getDmnRuleService();

    DmnHistoryService getDmnHistoryService();


}
