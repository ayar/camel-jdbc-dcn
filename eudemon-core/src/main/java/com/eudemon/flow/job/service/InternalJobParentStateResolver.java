package com.eudemon.flow.job.service;

import com.eudemon.flow.job.api.Job;

/**
 * @author martin.grofcik
 */
public interface InternalJobParentStateResolver {

    boolean isSuspended(Job job);
}
