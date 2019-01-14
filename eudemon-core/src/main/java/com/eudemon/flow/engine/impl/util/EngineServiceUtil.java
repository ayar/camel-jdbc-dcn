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
package com.eudemon.flow.engine.impl.util;

import com.eudemon.flow.cmmn.api.CmmnEngineConfigurationApi;
import com.eudemon.flow.cmmn.api.CmmnHistoryService;
import com.eudemon.flow.cmmn.api.CmmnManagementService;
import com.eudemon.flow.cmmn.api.CmmnRepositoryService;
import com.eudemon.flow.cmmn.api.CmmnRuntimeService;
import com.eudemon.flow.common.engine.impl.AbstractEngineConfiguration;
import com.eudemon.flow.common.engine.impl.interceptor.EngineConfigurationConstants;
import com.eudemon.flow.content.api.ContentEngineConfigurationApi;
import com.eudemon.flow.content.api.ContentService;
import com.eudemon.flow.dmn.api.DmnEngineConfigurationApi;
import com.eudemon.flow.dmn.api.DmnManagementService;
import com.eudemon.flow.dmn.api.DmnRepositoryService;
import com.eudemon.flow.dmn.api.DmnRuleService;
import com.eudemon.flow.engine.ProcessEngineConfiguration;
import com.eudemon.flow.form.api.FormEngineConfigurationApi;
import com.eudemon.flow.form.api.FormManagementService;
import com.eudemon.flow.form.api.FormRepositoryService;
import com.eudemon.flow.form.api.FormService;
import com.eudemon.flow.idm.api.IdmEngineConfigurationApi;
import com.eudemon.flow.idm.api.IdmIdentityService;

public class EngineServiceUtil {

    // IDM ENGINE

    public static IdmEngineConfigurationApi getIdmEngineConfiguration(AbstractEngineConfiguration engineConfiguration) {
        return (IdmEngineConfigurationApi) engineConfiguration.getEngineConfigurations().get(EngineConfigurationConstants.KEY_IDM_ENGINE_CONFIG);
    }

    public static IdmIdentityService getIdmIdentityService(AbstractEngineConfiguration engineConfiguration) {
        IdmIdentityService idmIdentityService = null;
        IdmEngineConfigurationApi idmEngineConfiguration = getIdmEngineConfiguration(engineConfiguration);
        if (idmEngineConfiguration != null) {
            idmIdentityService = idmEngineConfiguration.getIdmIdentityService();
        }

        return idmIdentityService;
    }

    // CMMN ENGINE

    public static CmmnEngineConfigurationApi getCmmnEngineConfiguration(AbstractEngineConfiguration engineConfiguration) {
        return (CmmnEngineConfigurationApi) engineConfiguration.getEngineConfigurations().get(EngineConfigurationConstants.KEY_CMMN_ENGINE_CONFIG);
    }

    public static CmmnRepositoryService getCmmnRepositoryService(AbstractEngineConfiguration engineConfiguration) {
        CmmnRepositoryService cmmnRepositoryService = null;
        CmmnEngineConfigurationApi cmmnEngineConfiguration = getCmmnEngineConfiguration(engineConfiguration);
        if (cmmnEngineConfiguration != null) {
            cmmnRepositoryService = cmmnEngineConfiguration.getCmmnRepositoryService();
        }

        return cmmnRepositoryService;
    }

    public static CmmnRuntimeService getCmmnRuntimeService(AbstractEngineConfiguration engineConfiguration) {
        CmmnRuntimeService cmmnRuntimeService = null;
        CmmnEngineConfigurationApi cmmnEngineConfiguration = getCmmnEngineConfiguration(engineConfiguration);
        if (cmmnEngineConfiguration != null) {
            cmmnRuntimeService = cmmnEngineConfiguration.getCmmnRuntimeService();
        }

        return cmmnRuntimeService;
    }

    public static CmmnHistoryService getCmmnHistoryService(AbstractEngineConfiguration engineConfiguration) {
        CmmnHistoryService cmmnHistoryService = null;
        CmmnEngineConfigurationApi cmmnEngineConfiguration = getCmmnEngineConfiguration(engineConfiguration);
        if (cmmnEngineConfiguration != null) {
            cmmnHistoryService = cmmnEngineConfiguration.getCmmnHistoryService();
        }

        return cmmnHistoryService;
    }

    public static CmmnManagementService getCmmnManagementService(AbstractEngineConfiguration engineConfiguration) {
        CmmnManagementService cmmnManagementService = null;
        CmmnEngineConfigurationApi cmmnEngineConfiguration = getCmmnEngineConfiguration(engineConfiguration);
        if (cmmnEngineConfiguration != null) {
            cmmnManagementService = cmmnEngineConfiguration.getCmmnManagementService();
        }

        return cmmnManagementService;
    }

    // DMN ENGINE

    public static DmnEngineConfigurationApi getDmnEngineConfiguration(AbstractEngineConfiguration engineConfiguration) {
        return (DmnEngineConfigurationApi) engineConfiguration.getEngineConfigurations().get(EngineConfigurationConstants.KEY_DMN_ENGINE_CONFIG);
    }

    public static DmnRepositoryService getDmnRepositoryService(AbstractEngineConfiguration engineConfiguration) {
        DmnRepositoryService dmnRepositoryService = null;
        DmnEngineConfigurationApi dmnEngineConfiguration = getDmnEngineConfiguration(engineConfiguration);
        if (dmnEngineConfiguration != null) {
            dmnRepositoryService = dmnEngineConfiguration.getDmnRepositoryService();
        }

        return dmnRepositoryService;
    }

    public static DmnRuleService getDmnRuleService(AbstractEngineConfiguration engineConfiguration) {
        DmnRuleService dmnRuleService = null;
        DmnEngineConfigurationApi dmnEngineConfiguration = getDmnEngineConfiguration(engineConfiguration);
        if (dmnEngineConfiguration != null) {
            dmnRuleService = dmnEngineConfiguration.getDmnRuleService();
        }

        return dmnRuleService;
    }

    public static DmnManagementService getDmnManagementService(AbstractEngineConfiguration engineConfiguration) {
        DmnManagementService dmnManagementService = null;
        DmnEngineConfigurationApi dmnEngineConfiguration = getDmnEngineConfiguration(engineConfiguration);
        if (dmnEngineConfiguration != null) {
            dmnManagementService = dmnEngineConfiguration.getDmnManagementService();
        }

        return dmnManagementService;
    }

    // FORM ENGINE

    public static FormEngineConfigurationApi getFormEngineConfiguration(AbstractEngineConfiguration engineConfiguration) {
        return (FormEngineConfigurationApi) engineConfiguration.getEngineConfigurations().get(EngineConfigurationConstants.KEY_FORM_ENGINE_CONFIG);
    }

    public static FormRepositoryService getFormRepositoryService(ProcessEngineConfiguration processEngineConfiguration) {
        FormRepositoryService formRepositoryService = null;
        FormEngineConfigurationApi formEngineConfiguration = getFormEngineConfiguration(processEngineConfiguration);
        if (formEngineConfiguration != null) {
            formRepositoryService = formEngineConfiguration.getFormRepositoryService();
        }

        return formRepositoryService;
    }

    public static FormService getFormService(AbstractEngineConfiguration engineConfiguration) {
        FormService formService = null;
        FormEngineConfigurationApi formEngineConfiguration = getFormEngineConfiguration(engineConfiguration);
        if (formEngineConfiguration != null) {
            formService = formEngineConfiguration.getFormService();
        }

        return formService;
    }

    public static FormManagementService getFormManagementService(AbstractEngineConfiguration engineConfiguration) {
        FormManagementService formManagementService = null;
        FormEngineConfigurationApi formEngineConfiguration = getFormEngineConfiguration(engineConfiguration);
        if (formEngineConfiguration != null) {
            formManagementService = formEngineConfiguration.getFormManagementService();
        }

        return formManagementService;
    }

    // CONTENT ENGINE

    public static ContentEngineConfigurationApi getContentEngineConfiguration(AbstractEngineConfiguration engineConfiguration) {
        return (ContentEngineConfigurationApi) engineConfiguration.getEngineConfigurations().get(EngineConfigurationConstants.KEY_CONTENT_ENGINE_CONFIG);
    }

    public static ContentService getContentService(AbstractEngineConfiguration engineConfiguration) {
        ContentService contentService = null;
        ContentEngineConfigurationApi contentEngineConfiguration = getContentEngineConfiguration(engineConfiguration);
        if (contentEngineConfiguration != null) {
            contentService = contentEngineConfiguration.getContentService();
        }

        return contentService;
    }

}
