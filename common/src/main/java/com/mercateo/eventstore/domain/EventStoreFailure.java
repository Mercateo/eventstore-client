package com.mercateo.eventstore.domain;

import org.immutables.value.Value;

import com.mercateo.immutables.DataClass;

import io.vavr.control.Option;

@Value.Immutable
@DataClass
public interface EventStoreFailure {

    static ImmutableEventStoreFailure.Builder builder() {
        return ImmutableEventStoreFailure.builder();
    }

    static EventStoreFailure of(Object data) {
        return builder().type(FailureType.INTERNAL_ERROR).setValueDataTBD(data).build();
    }

    FailureType getType();

    Option<Object> dataTBD();

    enum FailureType {
        UNKNOWN_EVENT_TYPE, NO_EVENTSTORE, NO_EVENTS, MULTIPLE_STREAMS, INTERNAL_ERROR
    }

}
