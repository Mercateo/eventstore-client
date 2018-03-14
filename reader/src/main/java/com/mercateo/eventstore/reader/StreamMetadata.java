package com.mercateo.eventstore.reader;

import com.mercateo.eventstore.domain.EventStreamId;
import org.immutables.value.Value;

import com.mercateo.eventstore.domain.EventNumber;
import com.mercateo.eventstore.domain.EventStoreName;
import com.mercateo.eventstore.domain.EventStreamName;
import com.mercateo.eventstore.domain.EventType;
import com.mercateo.immutables.Tuple;

@Value.Immutable
@Tuple
interface StreamMetadata {
    static StreamMetadata of(EventStreamId eventStreamId, EventNumber eventNumber,
                             EventType eventType) {
        return ImmutableStreamMetadata.of(eventStreamId, eventType, eventNumber);
    }

    EventStreamId eventStreamId();

    EventType eventType();

    EventNumber eventNumber();
}
