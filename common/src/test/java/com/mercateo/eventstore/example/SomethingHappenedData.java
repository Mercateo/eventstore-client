package com.mercateo.eventstore.example;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mercateo.eventstore.domain.EventData;
import com.mercateo.immutables.DataClass;

@Value.Immutable
@DataClass
@JsonSerialize(as = ImmutableSomethingHappenedData.class)
@JsonDeserialize(as = ImmutableSomethingHappenedData.class)
public interface SomethingHappenedData extends EventData {

    static ImmutableSomethingHappenedData.Builder builder() {
        return ImmutableSomethingHappenedData.builder();
    }

}
