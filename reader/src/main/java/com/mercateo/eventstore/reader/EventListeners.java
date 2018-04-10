package com.mercateo.eventstore.reader;

import java.util.Collections;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.github.msemys.esjc.EventStore;
import com.mercateo.eventstore.connection.EventStores;
import com.mercateo.eventstore.connection.EventStream;
import com.mercateo.eventstore.domain.EventStoreName;
import com.mercateo.eventstore.domain.EventStreamId;
import com.mercateo.eventstore.domain.EventStreamName;
import com.mercateo.eventstore.json.EventJsonMapper;

import io.vavr.Function2;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EventListeners {

    private final EventStores eventStores;

    private final MetadataMapper metadataMapper;

    private final EventJsonMapper eventJsonMapper;

    private final EventSubscriptionHealthIndicator healthIndicator;

    private final Map<EventStreamId, List<EventStreamListener>> listeners;

    private final Option<EventMetrics> metricsClient;

    public EventListeners(
            @SuppressWarnings("rawtypes") java.util.Optional<java.util.List<EventConsumer<?>>> recordedEventDataHandlers,
            Optional<EventMetrics> metricsClient, EventStores eventStores, MetadataMapper metadataMapper,
            EventJsonMapper eventJsonMapper, EventSubscriptionHealthIndicator healthIndicator) {
        this.metricsClient = metricsClient.map(Option::some).orElse(Option.none());
        this.eventStores = eventStores;
        this.metadataMapper = metadataMapper;
        this.eventJsonMapper = eventJsonMapper;
        this.healthIndicator = healthIndicator;

        listeners = List
            .ofAll(recordedEventDataHandlers.orElse(Collections.emptyList()))
            .groupBy(consumer -> consumer.eventStreamId().eventStoreName())
            .flatMap(this::createListeners)
            .groupBy(EventStreamListener::getEventStreamId)
            .mapValues(List::ofAll);
    }

    private Iterable<EventStreamListener> createListeners(Tuple2<EventStoreName, List<EventConsumer<?>>> entry) {
        val eventStoreName = entry._1();
        val consumers = entry._2();

        return eventStores.getEventStore(eventStoreName).map(eventStore -> {
            val createListener = Function2.of(this::createListeners2).apply(new EventStoreClient(eventStore,
                    eventStoreName));
            return consumers //
                .groupBy(EventConsumer::eventStreamId)
                .flatMap(createListener);
        }).onEmpty(() -> log.warn("undefined evenstore with name {}", eventStoreName)).getOrElse(List.empty());
    }

    private Iterable<EventStreamListener> createListeners2(EventStoreClient eventStoreClient,
            Tuple2<EventStreamId, List<EventConsumer<?>>> entry) {
        val eventStreamId = entry._1();
        val consumers = entry._2();
        val eventHandler = new EventHandler(consumers, eventJsonMapper, metadataMapper);
        val eventStream = eventStoreClient.createStream(eventStreamId.eventStreamName());
        val eventStatisticsCollector = new EventStatisticsCollector(eventStream, metricsClient);
        healthIndicator.addToMonitoring(eventStatisticsCollector);
        return List.of(new EventStreamListener(eventHandler, eventStream, eventStatisticsCollector));
    }

    public Option<EventStreamListener> getStreamListener(EventStreamId eventStreamId) {
        return listeners.get(eventStreamId).map(List::head).toOption();
    }

    public void subscribeStreamStartingAt(EventStreamId eventStreamId, long eventNumber) {
        getStreamListener(eventStreamId).forEach(stream -> stream.subscribe(eventNumber));
    }

    public void subscribeStream(EventStreamId eventStreamId) {
        getStreamListener(eventStreamId).forEach(EventStreamListener::subscribe);
    }

    public void stopSubscription(EventStreamId eventStreamId) {
        getStreamListener(eventStreamId).forEach(EventStreamListener::stopSubscription);
    }

    static class EventStoreClient {

        private final EventStore eventStore;

        private final EventStoreName eventStoreName;

        EventStoreClient(EventStore eventStore, EventStoreName eventStoreName) {
            this.eventStore = eventStore;
            this.eventStoreName = eventStoreName;
        }

        EventStream createStream(EventStreamName eventStreamName) {
            return new EventStream(eventStore, EventStreamId.of(eventStoreName, eventStreamName));
        }
    }

}
