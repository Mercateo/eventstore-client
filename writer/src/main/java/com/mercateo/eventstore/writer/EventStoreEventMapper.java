package com.mercateo.eventstore.writer;

import static com.mercateo.eventstore.domain.EventStoreFailure.FailureType.INTERNAL_ERROR;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.github.msemys.esjc.EventData;
import com.mercateo.eventstore.domain.Event;
import com.mercateo.eventstore.domain.EventStoreFailure;
import com.mercateo.eventstore.domain.EventType;
import com.mercateo.eventstore.json.EventJsonMapper;

import io.vavr.Tuple;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EventStoreEventMapper {

    private static final EventStoreFailure INTERNAL_FAILURE = EventStoreFailure.builder().type(INTERNAL_ERROR).build();

    private final EventJsonMapper eventJsonMapper;

    private final EventMetaDataMapper metaDataMapper;

    @SuppressWarnings("rawtypes")
    private final Map<EventType, EventConfiguration> configurations;

    public EventStoreEventMapper(@SuppressWarnings("rawtypes") Optional<java.util.List<EventConfiguration>> dataMappers,
                                 EventJsonMapper eventJsonMapper, EventMetaDataMapper metaDataMapper) {
        this.configurations = List//
            .ofAll(dataMappers.orElse(Collections.emptyList()))
            .groupBy(EventConfiguration::getType)
            .peek(x -> {
                if (x._2.length() > 1) {
                    log.warn("Found more than 1 EventDataMapper for the EventType {}", x._1);
                }
            })
            .mapValues(List::head);

        this.eventJsonMapper = eventJsonMapper;
        this.metaDataMapper = metaDataMapper;
    }

    public Either<EventStoreFailure, EventWriteData> toEventStoreEvent(Event event) {
        return Option
            .of(event)
            .onEmpty(() -> log.warn("Received a null event"))
            .toEither(INTERNAL_FAILURE)
            .flatMap(this::getMapper)
            .flatMap(mapper -> mapEvent(event, mapper))
            .peek(data -> log.info("toEventStoreEvent() {} - {}", new String(data.eventData().data, Charset.forName(
                    "utf8")), new String(data.eventData().metadata, Charset.forName("utf8"))));
    }

    @SuppressWarnings("rawtypes")
    private Either<EventStoreFailure, EventConfiguration> getMapper(final Event event) {
        return configurations
            .get(event.eventType()) //
            .onEmpty(() -> log.warn("No event mapper for {}", event))
            .toEither(INTERNAL_FAILURE);
    }

    @SuppressWarnings("unchecked")
    private Either<EventStoreFailure, EventWriteData> mapEvent(final Event event,
            @SuppressWarnings("rawtypes") EventConfiguration configuration) {
        Object writtenEvent = configuration.mapper().apply(event);
        return eventJsonMapper
            .toJsonString(writtenEvent)
            .flatMap(data -> metaDataMapper //
                .mapMetaData(event, configuration)
                .map(metadata -> Tuple.of(data, metadata)))
            .map(data -> EventWriteData.of(configuration.eventStreamId(), EventData
                .newBuilder()
                .type(event.eventType().value())
                .eventId(event.eventId().value())
                .jsonMetadata(data._2())
                .jsonData(data._1())
                .build()));
    }
}