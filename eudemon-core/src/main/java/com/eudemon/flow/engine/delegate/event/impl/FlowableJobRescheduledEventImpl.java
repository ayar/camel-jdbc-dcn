package com.eudemon.flow.engine.delegate.event.impl;

import com.eudemon.flow.common.engine.api.delegate.event.FlowableEngineEventType;
import com.eudemon.flow.engine.delegate.event.FlowableJobRescheduledEvent;
import com.eudemon.flow.job.api.Job;

public class FlowableJobRescheduledEventImpl extends FlowableEntityEventImpl implements FlowableJobRescheduledEvent {

    /**
     * The id of the original job that was rescheduled.
     */
    protected String rescheduledJobId;

    public FlowableJobRescheduledEventImpl(Job entity, String rescheduledJobId, FlowableEngineEventType type) {
        super(entity, type);
        this.rescheduledJobId = rescheduledJobId;
    }

    @Override
    public String getRescheduledJobId() {
        return rescheduledJobId;
    }
}
