package com.mercateo.eventstore.domain;

import javax.validation.constraints.NotNull;

import org.immutables.value.Value;

import com.mercateo.eventstore.data.EventInitiatorData;
import com.mercateo.immutables.DataClass;

import io.vavr.control.Option;

@Value.Immutable
@DataClass
public interface EventInitiator {

    static ImmutableEventInitiator.Builder builder() {
        return ImmutableEventInitiator.builder();
    }

    @NotNull
    static EventInitiator of(Reference initiator) {
        return EventInitiator.builder().initiator(initiator).build();
    }

    @NotNull
    static EventInitiator of(EventInitiatorData eventInitiatorData) {
        return EventInitiator
            .builder()
            .initiator(Reference.of(eventInitiatorData.initiator()))
            .impersonated(Option.of(eventInitiatorData.impersonated()).map(Reference::of))
            .build();
    }

    @NotNull
    Reference initiator();

    Option<Reference> impersonated();

}
