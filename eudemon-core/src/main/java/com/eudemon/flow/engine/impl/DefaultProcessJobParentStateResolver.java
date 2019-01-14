package com.eudemon.flow.engine.impl;

import org.apache.commons.lang3.StringUtils;
import com.eudemon.flow.common.engine.api.FlowableIllegalArgumentException;
import com.eudemon.flow.engine.impl.cfg.ProcessEngineConfigurationImpl;
import com.eudemon.flow.engine.runtime.ProcessInstance;
import com.eudemon.flow.job.api.Job;
import com.eudemon.flow.job.service.InternalJobParentStateResolver;

/**
 * @author martin.grofcik
 */
public class DefaultProcessJobParentStateResolver implements InternalJobParentStateResolver {
    protected ProcessEngineConfigurationImpl processEngineConfiguration;

    public DefaultProcessJobParentStateResolver(ProcessEngineConfigurationImpl processEngineConfiguration) {
        this.processEngineConfiguration = processEngineConfiguration;
    }

    @Override
    public boolean isSuspended(Job job) {
        if (StringUtils.isEmpty(job.getProcessInstanceId())) {
            throw new FlowableIllegalArgumentException("Job " + job.getId() + " parent is not process instance");
        }
        ProcessInstance processInstance = this.processEngineConfiguration.getRuntimeService().createProcessInstanceQuery().processInstanceId(job.getProcessInstanceId()).singleResult();
        return processInstance.isSuspended();
    }
}
