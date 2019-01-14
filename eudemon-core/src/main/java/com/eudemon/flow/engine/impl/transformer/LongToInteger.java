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
package com.eudemon.flow.engine.impl.transformer;

/**
 * Transforms a {@link Long} to a {@link Integer}
 *
 * @author Esteban Robles Luna
 */
public class LongToInteger extends AbstractTransformer {

    @Override
    protected Object primTransform(Object anObject) throws Exception {
        return Integer.valueOf(((Long) anObject).toString());
    }
}