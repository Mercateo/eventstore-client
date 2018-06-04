package com.mercateo.eventstore.writer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mercateo.eventstore.domain.EventData;
import com.mercateo.eventstore.example.ImmutableSomethingHappenedData;
import com.mercateo.immutables.DataClass;
import org.immutables.value.Value;

@Value.Immutable
@DataClass
@JsonSerialize(as = ImmutableSomethingHappenedData.class)
@JsonDeserialize(as = ImmutableSomethingHappenedData.class)
public interface Event1Data extends EventData {

    static ImmutableEvent1Data.Builder builder() {
        return ImmutableEvent1Data.builder();
    }

}
