package com.mercateo.eventstore.data;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mercateo.eventstore.domain.EventInitiator;
import com.mercateo.immutables.DataClass;

@Value.Immutable
@DataClass
@JsonSerialize(as = ImmutableEventInitiatorData.class)
@JsonDeserialize(as = ImmutableEventInitiatorData.class)
@JsonInclude(Include.NON_NULL)
public interface EventInitiatorData {

    static EventInitiatorData of(EventInitiator eventInitiator) {
        return ImmutableEventInitiatorData
            .builder()
            .initiator(ReferenceData.of(eventInitiator.initiator()))
            .impersonated(eventInitiator.impersonated().map(ReferenceData::of).getOrNull())
            .build();
    }

    @NotNull
    ReferenceData initiator();

    @Nullable
    ReferenceData impersonated();
}
