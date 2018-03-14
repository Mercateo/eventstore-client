package com.mercateo.eventstore.data;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

import java.util.UUID;

import javax.annotation.Nullable;

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
}
