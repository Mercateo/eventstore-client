package com.mercateo.eventstore.writer;

import com.mercateo.eventstore.domain.Event;
import com.mercateo.eventstore.domain.EventSchemaRef;
import com.mercateo.eventstore.domain.EventStreamId;
import com.mercateo.eventstore.domain.EventType;
import com.mercateo.eventstore.domain.EventVersion;

import io.vavr.Function1;

public interface EventConfiguration<E extends Event> {

    Function1<E, Object> mapper();

    EventStreamId eventStreamId();

    EventType getType();

    EventVersion eventVersion();

    EventSchemaRef eventSchemaRef();
}
