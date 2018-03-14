package com.mercateo.eventstore.domain;

import java.time.Instant;

import io.vavr.collection.List;

public interface Event {

    EventId eventId();

    EventType eventType();

    Instant timestamp();

    List<Causality> causality();
}
