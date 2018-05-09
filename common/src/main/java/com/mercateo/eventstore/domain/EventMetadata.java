package com.mercateo.eventstore.domain;

import org.immutables.value.Value;

import com.mercateo.immutables.DataClass;

import io.vavr.collection.List;
import io.vavr.control.Option;

@Value.Immutable
@DataClass
public interface EventMetadata {

    static ImmutableEventMetadata.Builder builder() {
        return ImmutableEventMetadata.builder();
    }

    EventStreamId eventStreamId();

    EventNumber eventNumber();

    EventType eventType();

    EventId eventId();

    Option<EventSchemaRef> eventSchemaRef();

    EventVersion version();

    List<Causality> causality();

    Option<EventInitiator> eventInitiator();
}
