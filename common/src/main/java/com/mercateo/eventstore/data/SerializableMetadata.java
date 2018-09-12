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
package com.mercateo.eventstore.data;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

import java.util.UUID;

import javax.annotation.Nullable;

import com.mercateo.eventstore.domain.EventInitiator;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mercateo.immutables.DataClass;

@Value.Immutable
@DataClass
@JsonSerialize(as = ImmutableSerializableMetadata.class)
@JsonDeserialize(as = ImmutableSerializableMetadata.class)
@JsonInclude(NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface SerializableMetadata {

    static ImmutableSerializableMetadata.Builder builder() {
        return ImmutableSerializableMetadata.builder();
    }

    UUID eventId();

    @Nullable
    UUID correlationId();

    @Nullable
    JsonNode schema();

    @Nullable
    String schemaRef();

    @Nullable
    Integer version();

    @Nullable
    String eventType();

    @Nullable
    CausalityData[] causality();

    @Nullable
    EventInitiatorData eventInitiator();

}
