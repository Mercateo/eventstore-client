package com.mercateo.eventstore.domain;

import org.immutables.value.Value;

import com.mercateo.immutables.DataClass;

@Value.Immutable
@DataClass
public interface Causality {

    static ImmutableCausality.Builder builder() {
        return ImmutableCausality.builder();
    }

    static Causality of(Event cause) {
        return Causality.builder().eventId(cause.eventId()).eventType(cause.eventType()).build();
    }

    EventId eventId();

    EventType eventType();
}
