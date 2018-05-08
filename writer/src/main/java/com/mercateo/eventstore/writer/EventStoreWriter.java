package com.mercateo.eventstore.writer;

import org.springframework.stereotype.Component;

import com.mercateo.eventstore.domain.Event;
import com.mercateo.eventstore.domain.EventStoreFailure;
import com.mercateo.eventstore.writer.domain.EventWriter;

import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

@Slf4j
@Component("eventStoreWriter")
@AllArgsConstructor
public class EventStoreWriter implements EventWriter {

    private final EventStoreEventMapper eventMapper;

    private final EventSender eventSender;

    private void logFailure(Iterable<? extends Event> events, EventStoreFailure failure) {
        events.forEach(event -> log.error("Event {} not saved: {}", event, failure));
    }

    @Override
    public <E extends Event> Either<EventStoreFailure, E> write(E event) {
        return write(Collections.singleton(event)).map(ignore -> event);
    }

    @Override
    public <E extends Iterable<? extends Event>> Either<EventStoreFailure, E> write(E events) {
        return eventMapper
            .toEventStoreEvent(events)
            .flatMap(eventSender::send)
            .peek(r -> logSuccess(events))
            .peekLeft(f -> logFailure(events, f))
            .map(ignore -> events);
    }

    private void logSuccess(Iterable<? extends Event> events) {
        events.forEach(event -> log.info("Event {} saved", event));
    }
}