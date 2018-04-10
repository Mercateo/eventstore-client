package com.mercateo.eventstore.example;

import java.net.URI;

import org.immutables.value.Value;

import com.mercateo.eventstore.domain.Event;
import com.mercateo.eventstore.domain.EventSchemaRef;
import com.mercateo.eventstore.domain.EventStoreName;
import com.mercateo.eventstore.domain.EventStreamId;
import com.mercateo.eventstore.domain.EventStreamName;
import com.mercateo.eventstore.domain.EventType;
import com.mercateo.eventstore.domain.EventVersion;
import com.mercateo.immutables.DataClass;

@Value.Immutable
@DataClass
public interface SomethingHappened extends Event {

    EventStreamId EVENT_STREAM_ID = EventStreamId.of(EventStoreName.of("default"), EventStreamName.of("test"));

    EventType EVENT_TYPE = EventType.of("something-happened");

    EventVersion EVENT_VERSION = EventVersion.of(3);

    EventSchemaRef EVENT_SCHEMA_REF = EventSchemaRef.of(URI.create("https://test.com/ref"));

    static ImmutableSomethingHappened.Builder builder() {
        return ImmutableSomethingHappened.builder();
    }

    @Override
    default EventType eventType() {
        return EVENT_TYPE;
    }
}
