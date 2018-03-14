package com.mercateo.eventstore.writer;

import org.springframework.stereotype.Component;

import com.mercateo.eventstore.domain.Event;
import com.mercateo.eventstore.domain.EventStoreFailure;
import com.mercateo.eventstore.writer.domain.EventWriter;

import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class EventStoreWriter implements EventWriter {

    private final EventStoreEventMapper eventMapper;

    private final EventSender eventSender;

    private void logFailure(Event event, EventStoreFailure failure) {
        log.error("Event {} not saved: {}", event, failure);
    }

    @Override
    public <E extends Event> Either<EventStoreFailure, E> write(E event) {
        return eventMapper
            .toEventStoreEvent(event)
            .flatMap(eventSender::send)
            .peek(r -> logSuccess(event))
            .peekLeft(f -> logFailure(event, f))
            .map(ignore -> event);
    }

    private void logSuccess(Event event) {
        log.info("Event {} saved", event);
    }
}