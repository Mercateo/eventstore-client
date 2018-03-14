package com.mercateo.eventstore.reader;

import com.mercateo.eventstore.domain.EventStreamId;

public interface EventMetrics {
    void onReplayFinished(EventStreamId eventStreamId, long eventCount, double eventRate, double eventDataRate);
    void onEvent(EventStreamId eventStreamId);
}
