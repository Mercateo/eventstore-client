package com.mercateo.eventstore.writer.example;

import static com.mercateo.eventstore.example.SomethingHappened.EVENT_SCHEMA_REF;
import static com.mercateo.eventstore.example.SomethingHappened.EVENT_STREAM_ID;
import static com.mercateo.eventstore.example.SomethingHappened.EVENT_TYPE;
import static com.mercateo.eventstore.example.SomethingHappened.EVENT_VERSION;

import com.mercateo.eventstore.example.SomethingHappenedData;
import org.springframework.stereotype.Component;

import com.mercateo.eventstore.domain.EventSchemaRef;
import com.mercateo.eventstore.domain.EventStreamId;
import com.mercateo.eventstore.domain.EventType;
import com.mercateo.eventstore.domain.EventVersion;
import com.mercateo.eventstore.example.SomethingHappened;
import com.mercateo.eventstore.writer.EventConfiguration;

import io.vavr.Function1;

@Component
class SomethingHappenedEventConfiguration implements EventConfiguration<SomethingHappened> {

    public SomethingHappenedData map(SomethingHappened somethingHappened) {
        return SomethingHappenedData.builder().timestamp(somethingHappened.timestamp()).build();
    }

    @Override
    public EventStreamId eventStreamId() {
        return EVENT_STREAM_ID;
    }

    @Override
    public EventType getType() {
        return EVENT_TYPE;
    }

    @Override
    public EventVersion eventVersion() {
        return EVENT_VERSION;
    }

    @Override
    public EventSchemaRef eventSchemaRef() {
        return EVENT_SCHEMA_REF;
    }

    @Override
    public Function1<SomethingHappened, Object> mapper() {
        return this::map;
    }

}