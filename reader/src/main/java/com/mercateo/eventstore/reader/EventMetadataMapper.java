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
package com.mercateo.eventstore.reader;

import java.net.URI;

import org.springframework.stereotype.Component;

import com.mercateo.eventstore.data.CausalityData;
import com.mercateo.eventstore.data.SerializableMetadata;
import com.mercateo.eventstore.domain.Causality;
import com.mercateo.eventstore.domain.EventId;
import com.mercateo.eventstore.domain.EventInitiator;
import com.mercateo.eventstore.domain.EventMetadata;
import com.mercateo.eventstore.domain.EventSchemaRef;
import com.mercateo.eventstore.domain.EventStoreFailure;
import com.mercateo.eventstore.domain.EventType;
import com.mercateo.eventstore.domain.EventVersion;
import com.mercateo.eventstore.domain.ImmutableEventMetadata;

import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Component("eventMetadataMapper")
@Slf4j
public class EventMetadataMapper {

    public static final EventVersion LEGACY_VERSION = EventVersion.of(0);

    public Either<EventStoreFailure, EventMetadata> mapMetadata(StreamMetadata streamMetadata,
            SerializableMetadata serializableMetadata) {
        val builder = EventMetadata
            .builder()
            .eventStreamId(streamMetadata.eventStreamId())
            .eventType(streamMetadata.eventType())
            .eventNumber(streamMetadata.eventNumber())
            .eventId(EventId.of(serializableMetadata.eventId()))
            .causality(mapCausality(serializableMetadata.causality()))
            .eventInitiator(mapInitiator(serializableMetadata));

        if (serializableMetadata.version() == null) {
            return mapLegacyMetadata(serializableMetadata, builder);
        } else {
            return mapMetadata(serializableMetadata, builder);
        }
    }

    private Option<EventInitiator> mapInitiator(SerializableMetadata serializableMetadata) {
        return Option.of(serializableMetadata.eventInitiator()).map(EventInitiator::of);
    }

    private List<Causality> mapCausality(CausalityData[] causalityData) {
        return Option //
            .of(causalityData)
            .toList()
            .flatMap(List::of)
            .map(entry -> (Causality) Causality //
                .builder()
                .eventType(EventType.of(entry.eventType()))
                .eventId(EventId.of(entry.eventId()))
                .build());
    }

    private Either<EventStoreFailure, EventMetadata> mapLegacyMetadata(
            @SuppressWarnings("unused") SerializableMetadata serializableMetadata,
            ImmutableEventMetadata.Builder builder) {
        builder.version(LEGACY_VERSION);
        return Either.right(builder.build());
    }

    private Either<EventStoreFailure, EventMetadata> mapMetadata(SerializableMetadata serializableMetadata,
            ImmutableEventMetadata.Builder builder) {
        return Try
            .success(builder)
            .map(b -> b.version(EventVersion.of(serializableMetadata.version())))
            .mapTry(b -> b.setValueEventSchemaRef(EventSchemaRef.of(URI.create(serializableMetadata.schemaRef()))))
            .onFailure(e -> log.warn("error converting eventSchemaRef '{}'", serializableMetadata.schemaRef(), e))
            .map(b -> (EventMetadata) b.build())
            .toEither()
            .mapLeft(EventStoreFailure::of);
    }
}
