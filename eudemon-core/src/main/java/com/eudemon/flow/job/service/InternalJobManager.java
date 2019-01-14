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

package com.eudemon.flow.job.service;

import com.eudemon.flow.job.api.Job;
import com.eudemon.flow.job.api.JobEntity;
import com.eudemon.flow.job.api.TimerJobEntity;
import com.eudemon.flow.variable.api.delegate.VariableScope;

/**
 * @author Tijs Rademakers
 */
public interface InternalJobManager {

    VariableScope resolveVariableScope(Job job);

    boolean handleJobInsert(Job job);

    void handleJobDelete(Job job);

    void lockJobScope(Job job);

    void clearJobScopeLock(Job job);

    void preTimerJobDelete(JobEntity jobEntity, VariableScope variableScope);

    void preRepeatedTimerSchedule(TimerJobEntity timerJobEntity, VariableScope variableScope);

}