package com.mercateo.eventstore.writer;

import org.springframework.stereotype.Component;

import com.mercateo.eventstore.data.CausalityData;
import com.mercateo.eventstore.data.SerializableMetadata;
import com.mercateo.eventstore.domain.Event;
import com.mercateo.eventstore.domain.EventStoreFailure;
import com.mercateo.eventstore.json.JsonMapper;

import io.vavr.Function2;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.val;

@Component
@AllArgsConstructor
class EventMetaDataMapper {

    private final JsonMapper jsonMapper;

    Either<EventStoreFailure, String> mapMetaData(Event event, EventConfiguration dataMapper) {
        val toMetaData = Function2.of(this::toMetaData).apply(dataMapper);

        return Either //
            .<EventStoreFailure, Event> right(event)
            .map(toMetaData)
            .flatMap(jsonMapper::toJsonString);
    }

    private SerializableMetadata toMetaData(EventConfiguration dataMapper, Event event) {
        return SerializableMetadata
            .builder()
            .eventId(event.eventId().value())
            .eventType(event.eventType().value())
            .schemaRef(dataMapper.eventSchemaRef().value().toString())
            .version(dataMapper.eventVersion().value())
            .causality(event.causality().map(CausalityData::of).toJavaArray(CausalityData.class))
            .build();
    }
}
