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
package com.eudemon.flow.cmmn.engine;

import com.eudemon.flow.cmmn.api.CmmnHistoryService;
import com.eudemon.flow.cmmn.api.CmmnManagementService;
import com.eudemon.flow.cmmn.api.CmmnRepositoryService;
import com.eudemon.flow.cmmn.api.CmmnRuntimeService;
import com.eudemon.flow.cmmn.api.CmmnTaskService;

/**
 * Provides access to all services that expose CMMN and case management operations.
 *
 * @author Joram Barrez
 */
public interface CmmnEngine {


    String getName();

    void close();

    CmmnRuntimeService getCmmnRuntimeService();

    CmmnTaskService getCmmnTaskService();

    CmmnManagementService getCmmnManagementService();

    CmmnRepositoryService getCmmnRepositoryService();

    CmmnHistoryService getCmmnHistoryService();

}
