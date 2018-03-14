package com.mercateo.eventstore.writer.domain;

import com.mercateo.eventstore.domain.Event;
import com.mercateo.eventstore.domain.EventStoreFailure;

import io.vavr.control.Either;

public interface EventWriter {
    <E extends Event> Either<EventStoreFailure, E> write(E event);
}
