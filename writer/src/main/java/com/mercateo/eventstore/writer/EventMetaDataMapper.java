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

import org.springframework.stereotype.Component;

import com.mercateo.eventstore.data.CausalityData;
import com.mercateo.eventstore.data.EventInitiatorData;
import com.mercateo.eventstore.data.SerializableMetadata;
import com.mercateo.eventstore.domain.Event;
import com.mercateo.eventstore.domain.EventStoreFailure;
import com.mercateo.eventstore.json.EventJsonMapper;

import io.vavr.Function2;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.val;

@Component("eventMetaDataMapper")
@AllArgsConstructor
public class EventMetaDataMapper {

    private final EventJsonMapper eventJsonMapper;

    public Either<EventStoreFailure, String> mapMetaData(Event event, EventConfiguration dataMapper) {
        val toMetaData = Function2.of(this::toMetaData).apply(dataMapper);

        return Either //
            .<EventStoreFailure, Event> right(event)
            .map(toMetaData)
            .flatMap(eventJsonMapper::toJsonString);
    }

    private SerializableMetadata toMetaData(EventConfiguration config, Event event) {
        return SerializableMetadata
            .builder()
            .eventId(event.eventId().value())
            .eventType(event.eventType().value())
            .schemaRef(config.eventSchemaRef().value().toString())
            .version(config.eventVersion().value())
            .causality(event.causality().map(CausalityData::of).toJavaArray(CausalityData.class))
            .eventInitiator(EventInitiatorData.of(event.eventInitiator().orElse(null)))
            .build();
    }
}
