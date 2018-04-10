package com.mercateo.eventstore.writer;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.mercateo.eventstore.connection.EventStores;
import com.mercateo.eventstore.domain.EventStoreFailure;
import com.mercateo.eventstore.domain.EventStoreFailure.FailureType;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("eventSender")
public class EventSender {

    public static final EventStoreFailure NO_EVENT_STORE_FAILURE = EventStoreFailure
        .builder()
        .type(FailureType.NO_EVENTSTORE)
        .build();

    private final EventStores eventstores;

    private final int eventstoreTimeoutMs;

    EventSender(EventStores eventstores) {
        this.eventstores = eventstores;
        this.eventstoreTimeoutMs = 5000;
    }

    private static EventStoreFailure apply(Throwable throwable) {
        return EventStoreFailure.builder().type(FailureType.INTERNAL_ERROR).setValueDataTBD(throwable).build();
    }

    public Either<EventStoreFailure, Void> send(EventWriteData event) {
        Function<Throwable, EventStoreFailure> mapFailure = EventSender::apply;

        return eventstores.getEventStream(event.eventStreamId()).toEither(NO_EVENT_STORE_FAILURE).flatMap(
                eventStream -> Try
                    .of(() -> eventStream.append(event.eventData()).get(eventstoreTimeoutMs, TimeUnit.MILLISECONDS))
                    .onFailure(e -> log.error("could not send event", e))
                    .toEither()
                    .mapLeft(mapFailure)
                    .map(ignore -> null));
    }
}