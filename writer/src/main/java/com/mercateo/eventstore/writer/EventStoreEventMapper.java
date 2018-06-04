/**
 * Copyright © 2018 Mercateo AG (http://www.mercateo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mercateo.eventstore.writer;

import static com.mercateo.eventstore.domain.EventStoreFailure.FailureType.MULTIPLE_STREAMS;
import static com.mercateo.eventstore.domain.EventStoreFailure.FailureType.NO_EVENTS;
import static com.mercateo.eventstore.domain.EventStoreFailure.FailureType.UNKNOWN_EVENT_TYPE;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.github.msemys.esjc.EventData;
import com.mercateo.eventstore.domain.Event;
import com.mercateo.eventstore.domain.EventStoreFailure;
import com.mercateo.eventstore.domain.EventStreamId;
import com.mercateo.eventstore.domain.EventType;
import com.mercateo.eventstore.json.EventJsonMapper;

import io.vavr.Tuple;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EventStoreEventMapper {

    private static final EventStoreFailure UNKNOWN_EVENT_TYPE_FAILURE = EventStoreFailure
        .builder()
        .type(UNKNOWN_EVENT_TYPE)
        .build();

    private static final EventStoreFailure NO_EVENTS_FAILURE = EventStoreFailure.builder().type(NO_EVENTS).build();

    private static final EventStoreFailure MULTIPLE_STREAMS_FAILURE = EventStoreFailure
        .builder()
        .type(MULTIPLE_STREAMS)
        .build();

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

    public Either<EventStoreFailure, EventWriteData> toEventStoreEvent(Iterable<? extends Event> events) {
        return mapEvents(Option.of(events).<List<? extends Event>> map(List::ofAll).getOrElse(List.empty()));
    }

    private Either<EventStoreFailure, EventWriteData> mapEvents(final List<? extends Event> events) {

        if (events.isEmpty()) {
            return Either.left(NO_EVENTS_FAILURE);
        }

        final Map<Option<EventStreamId>, ? extends List<? extends Event>> eventsByStream = events //
            .groupBy(event -> configurations.get(event.eventType()).map(EventConfiguration::eventStreamId));

        if (eventsByStream.keySet().size() > 1) {
            return Either.left(MULTIPLE_STREAMS_FAILURE);
        }

        val streamIdOption = eventsByStream.keySet().head();

        if (streamIdOption.isEmpty()) {
            return Either.left(UNKNOWN_EVENT_TYPE_FAILURE);
        }

        return mapEventsByStream(events, streamIdOption.get());
    }

    private Either<EventStoreFailure, EventWriteData> mapEventsByStream(final List<? extends Event> events,
            EventStreamId eventStreamId) {

        final List<Either<EventStoreFailure, EventData>> mappedData = events.map(event -> mapEvent(configurations
            .get(event.eventType())
            .get(), event));

        return mappedData
            .find(Either::isLeft)
            .map(Either::getLeft)
            .<Either<EventStoreFailure, EventWriteData>> map(Either::left)
            .getOrElse(() -> Either.right(EventWriteData.of( //
                    eventStreamId, //
                    mappedData //
                        .map(Either::get)
                        .peek(eventData -> log.info("toEventStoreEvent() {} - {}", new String(eventData.data, Charset
                            .forName("utf8")), new String(eventData.metadata, Charset.forName("utf8")))))));
    }

    @SuppressWarnings("unchecked")
    private Either<EventStoreFailure, EventData> mapEvent(
            @SuppressWarnings("rawtypes") EventConfiguration configuration, final Event event) {
        Object writtenEvent = configuration.mapper().apply(event);
        return eventJsonMapper
            .toJsonString(writtenEvent)
            .flatMap(data -> metaDataMapper //
                .mapMetaData(event, configuration)
                .map(metadata -> Tuple.of(data, metadata)))
            .map(data -> EventData
                .newBuilder()
                .type(event.eventType().value())
                .eventId(event.eventId().value())
                .jsonMetadata(data._2())
                .jsonData(data._1())
                .build());
    }
}