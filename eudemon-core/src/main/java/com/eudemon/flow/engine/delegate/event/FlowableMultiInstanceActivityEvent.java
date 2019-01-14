package com.eudemon.flow.engine.delegate.event;

import com.eudemon.flow.common.engine.api.delegate.event.FlowableEvent;

/**
 * A {@link FlowableEvent} related to a multi-instance activity within an execution.
 *
 * @author Robert Hafner
 */
public interface FlowableMultiInstanceActivityEvent extends FlowableActivityEvent {
    public boolean isSequential();
}
