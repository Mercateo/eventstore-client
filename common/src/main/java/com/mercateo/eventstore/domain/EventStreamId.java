package com.mercateo.eventstore.domain;

import com.mercateo.immutables.Tuple;
import org.immutables.value.Value;

@Value.Immutable
@Tuple
public abstract class EventStreamId {

    public abstract EventStoreName eventStoreName();

    public abstract EventStreamName eventStreamName();

    @Override
    public String toString() {
        return "EventStreamId{" + eventStoreName() + "/" + eventStreamName() + "}";
    }

    public static EventStreamId of(EventStoreName eventStoreName, EventStreamName eventStreamName) {
        return ImmutableEventStreamId.of(eventStoreName, eventStreamName);
    }
}
