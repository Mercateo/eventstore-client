package com.mercateo.eventstore.writer;

import java.util.Collections;

import org.immutables.value.Value;

import com.github.msemys.esjc.EventData;
import com.mercateo.eventstore.domain.EventStreamId;
import com.mercateo.immutables.Tuple;

@Value.Immutable
@Tuple
public interface EventWriteData {

    static EventWriteData of(EventStreamId eventStreamId, EventData eventData) {
        return ImmutableEventWriteData.of(eventStreamId, Collections.singleton(eventData));
    }

    static EventWriteData of(EventStreamId eventStreamId, Iterable<EventData> eventData) {
        return ImmutableEventWriteData.of(eventStreamId, eventData);
    }

    EventStreamId eventStreamId();

    Iterable<EventData> eventData();
}
