package com.eudemon.flow.engine.delegate.event;

import com.eudemon.flow.common.engine.api.delegate.event.FlowableEntityEvent;

public interface FlowableJobRescheduledEvent extends FlowableEntityEvent {
    /**
     * @return the job id of the original job that was rescheduled
     */
    public String getRescheduledJobId();
}
