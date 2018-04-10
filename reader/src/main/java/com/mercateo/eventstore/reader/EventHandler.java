package com.mercateo.eventstore.reader;

import java.nio.charset.Charset;

import com.github.msemys.esjc.RecordedEvent;
import com.github.msemys.esjc.ResolvedEvent;
import com.mercateo.eventstore.data.SerializableMetadata;
import com.mercateo.eventstore.domain.EventMetadata;
import com.mercateo.eventstore.domain.EventNumber;
import com.mercateo.eventstore.domain.EventStoreFailure;
import com.mercateo.eventstore.domain.EventType;
import com.mercateo.eventstore.json.EventJsonMapper;

import io.vavr.Function2;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class EventHandler {

    private final EventJsonMapper eventJsonMapper;

    private final MetadataMapper metadataMapper;

    private final Map<String, List<EventConsumer<?>>> consumers;

    private final Map<Class<?>, List<EventConsumer<?>>> consumersBySerializableType;

    public EventHandler(List<EventConsumer<?>> consumers, EventJsonMapper eventJsonMapper, MetadataMapper metadataMapper) {
        this.consumers = consumers.groupBy(EventConsumer::eventType).mapKeys(EventType::value);
        consumersBySerializableType = consumers.groupBy(EventConsumer::getSerializableDataType);
        this.eventJsonMapper = eventJsonMapper;
        this.metadataMapper = metadataMapper;
    }

    public void onEvent(ResolvedEvent event) {
        val recordedEvent = event.event;

        log.info("onEvent() {} - {}", new String(recordedEvent.data, Charset.forName("utf8")), new String(
                recordedEvent.metadata, Charset.forName("utf8")));

        consumers
            .get(recordedEvent.eventType) //
            .forEach(consumers -> consumers.forEach(consumer -> mapAndNotify(consumer, recordedEvent)));
    }

    private void mapAndNotify(EventConsumer<?> consumer, RecordedEvent recordedEvent) {
        val mapData = Function2.of(this::mapData).apply(consumer.getSerializableDataType());
        val streamMetadata = StreamMetadata.of(consumer.eventStreamId(), EventNumber.of(
                recordedEvent.eventNumber), EventType.of(recordedEvent.eventType));
        val mapMetadata = Function2.of(this::mapMetadata).apply(streamMetadata);

        Either. //
        <EventStoreFailure, RecordedEvent> right(recordedEvent)
            .map(event -> Tuple.of(event.data, event.metadata))
            .flatMap(mapData)
            .flatMap(mapMetadata)
            .peek(tuple -> notifyChecked(consumer, tuple._1(), tuple._2()))
            .peekLeft(error -> log.warn("error deserializing {}", error));
    }

    private Either<EventStoreFailure, Tuple2<Object, byte[]>> mapData(Class<?> serializableType,
            Tuple2<byte[], byte[]> tuple) {
        return eventJsonMapper //
            .readValue(tuple._1(), serializableType)
            .map(event -> Tuple.of(event, tuple._2()));
    }

    private Either<EventStoreFailure, Tuple2<Object, EventMetadata>> mapMetadata(StreamMetadata streamMetadata,
            Tuple2<Object, byte[]> tuple) {
        val mapMetadata = Function2.of(metadataMapper::mapMetadata).apply(streamMetadata);

        return eventJsonMapper //
            .readValue(tuple._2(), SerializableMetadata.class)
            .flatMap(mapMetadata)
            .map(metadata -> Tuple.of(tuple._1(), metadata));
    }

    private void notifyChecked(EventConsumer consumer, Object data, EventMetadata metadata) {
        // noinspection unchecked
        Try //
            .run(() -> consumer.onEvent(data, metadata))
            .onFailure(e -> log.warn("internal error in event consumer {}", consumer.getClass().getSimpleName(), e));
    }

    public int consumerCountFor(Class<?> clazz) {
        return consumersBySerializableType.get(clazz).map(List::size).getOrElse(0);
    }
}