package com.mercateo.eventstore.reader;

import com.mercateo.eventstore.domain.EventMetadata;
import com.mercateo.eventstore.domain.EventStreamId;
import com.mercateo.eventstore.domain.EventType;

public interface EventConsumer<D> {

    EventStreamId eventStreamId();

    EventType eventType();

    void onEvent(D data, EventMetadata metadata);

    Class<? extends D> getSerializableDataType();
}
