package com.mercateo.eventstore.data;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mercateo.eventstore.domain.Causality;
import com.mercateo.immutables.DataClass;

@Value.Immutable
@DataClass
@JsonSerialize(as = ImmutableCausalityData.class)
@JsonDeserialize(as = ImmutableCausalityData.class)
public interface CausalityData {

    static CausalityData of(Causality causality) {
        return ImmutableCausalityData
            .builder()
            .eventId(causality.eventId().value())
            .eventType(causality.eventType().value())
            .build();
    }

    @NotNull
    UUID eventId();

    @NotNull
    String eventType();
}
