package com.mercateo.eventstore.connection;

import java.net.InetSocketAddress;

import org.springframework.stereotype.Component;

import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.EventStoreBuilder;
import com.mercateo.eventstore.config.EventStoreProperties;
import com.mercateo.eventstore.domain.EventStoreFailure;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("eventStoreFactory")
public class EventStoreFactory {
    public Either<EventStoreFailure, EventStore> createEventStore(EventStoreProperties properties) {
        return Try.of(() -> createEventStoreInternal(properties)).toEither().mapLeft(this::mapError);
    }

    private EventStore createEventStoreInternal(EventStoreProperties properties) {
        log.info("create EvenStore client named {} with {}@{}:{}", properties.getName(), properties.getUsername(),
                properties.getHost(), properties.getPort());

        return EventStoreBuilder
            .newBuilder()
            .singleNodeAddress(InetSocketAddress.createUnresolved(properties.getHost(), properties.getPort()))
            .userCredentials(properties.getUsername(), properties.getPassword())
            .maxReconnections(-1)
            .maxOperationRetries(-1)
            .build();
    }

    private EventStoreFailure mapError(Throwable t) {
        return EventStoreFailure.of(t);
    }
}
