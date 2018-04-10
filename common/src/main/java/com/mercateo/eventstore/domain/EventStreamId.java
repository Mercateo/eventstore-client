package com.mercateo.eventstore.domain;

import org.immutables.value.Value;

import com.mercateo.immutables.Tuple;

@Value.Immutable
@Tuple
public abstract class EventStreamId {

    public static EventStreamId of(EventStoreName eventStoreName, EventStreamName eventStreamName) {
        return ImmutableEventStreamId.of(eventStoreName, eventStreamName);
    }

    public abstract EventStoreName eventStoreName();

    public abstract EventStreamName eventStreamName();

    @Override
    public String toString() {
        return "EventStreamId{" + eventStoreName() + "/" + eventStreamName() + "}";
    }
}
