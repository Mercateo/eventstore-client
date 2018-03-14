package com.mercateo.eventstore.connection;

import java.util.concurrent.ConcurrentHashMap;

import com.mercateo.eventstore.domain.EventStreamId;
import org.springframework.stereotype.Component;

import com.github.msemys.esjc.EventStore;
import com.mercateo.eventstore.config.EventStoreProperties;
import com.mercateo.eventstore.config.EventStorePropertiesCollection;
import com.mercateo.eventstore.domain.EventStoreName;
import com.mercateo.eventstore.domain.EventStreamName;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EventStores {

    private final EventStoreFactory factory;

    private Map<EventStoreName, EventStoreProperties> eventStoreProperties;

    private ConcurrentHashMap<EventStoreName, EventStore> eventstores;

    private ConcurrentHashMap<EventStreamId, EventStream> eventstreams;

    public EventStores(EventStoreFactory factory, EventStorePropertiesCollection properties) {
        this.factory = factory;
        eventstores = new ConcurrentHashMap<>();
        eventstreams = new ConcurrentHashMap<>();

        eventStoreProperties = List
            .ofAll(properties.getEventstores())
            .groupBy(EventStoreProperties::getName)
            .mapKeys(EventStoreName::of)
            .mapValues(List::head);
    }

    public Option<EventStore> getEventStore(EventStoreName eventStoreName) {
        return Option.of(eventstores.computeIfAbsent(eventStoreName, name -> createEventStore(name).getOrNull()));
    }

    public Option<EventStream> getEventStream(EventStreamId eventStreamId) {
        return Option.of(eventstreams.computeIfAbsent(eventStreamId,
                definition -> getEventStore(eventStreamId.eventStoreName())
                    .map(eventStore -> new EventStream(eventStore, eventStreamId))
                    .getOrNull()));
    }

    private Option<EventStore> createEventStore(EventStoreName eventStoreName) {
        return eventStoreProperties
            .get(eventStoreName)
            .map(factory::createEventStore)
            .peek(store -> store.peekLeft(error -> log.error("could not create event store client with name {}",
                    eventStoreName)))
            .flatMap(Either::toOption);
    }
}
