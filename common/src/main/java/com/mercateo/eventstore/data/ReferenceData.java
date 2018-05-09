package com.mercateo.eventstore.data;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mercateo.eventstore.domain.Reference;
import com.mercateo.immutables.DataClass;

@Value.Immutable
@DataClass
@JsonSerialize(as = ImmutableReferenceData.class)
@JsonDeserialize(as = ImmutableReferenceData.class)
public interface ReferenceData {

    static ReferenceData of(Reference reference) {
        return ImmutableReferenceData.builder().id(reference.id()).type(reference.type()).build();
    }

    @NotNull
    UUID id();

    @NotNull
    String type();

}
