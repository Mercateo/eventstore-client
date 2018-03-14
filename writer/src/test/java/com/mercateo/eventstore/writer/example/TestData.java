package com.mercateo.eventstore.writer.example;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

import org.json.JSONObject;

import com.mercateo.eventstore.domain.Causality;
import com.mercateo.eventstore.domain.EventId;
import com.mercateo.eventstore.domain.EventSchemaRef;
import com.mercateo.eventstore.domain.EventStoreFailure;
import com.mercateo.eventstore.domain.EventType;
import com.mercateo.eventstore.domain.EventVersion;
import com.mercateo.eventstore.example.SomethingHappened;

public final class TestData {
    public static final String STREAM_NAME = "test-stream";

    public static final EventStoreFailure INTERNAL_ERROR_FAILURE = EventStoreFailure
        .builder()
        .type(EventStoreFailure.FailureType.INTERNAL_ERROR)
        .build();

    public static final UUID EVENT_UUID = UUID.fromString("55555555-5555-5555-5555-555555555551");

    public static final String EVENT_TYPE_STRING = "something-happened";

    public static final EventType EVENT_TYPE = EventType.of(EVENT_TYPE_STRING);

    public static final EventId EVENT_ID = EventId.of(EVENT_UUID);

    public static final EventVersion EVENT_VERSION = EventVersion.of(5);

    public static final String EVENT_SCHEMA_REF_STRING = "https://awesomeschema.org/something-happened_5.json";

    public static final EventSchemaRef EVENT_SCHEMA_REF = EventSchemaRef.of(URI.create(EVENT_SCHEMA_REF_STRING));

    public static final EventId EVENT_ID_3 = EventId.of(UUID.fromString("55555555-5555-5555-5555-555555555553"));

    public static final Instant TIMESTAMP = Instant.ofEpochSecond(0);

    public static final SomethingHappened SOMETHING_HAPPENED = SomethingHappened
        .builder()
        .eventId(EVENT_ID_3)
        .timestamp(TIMESTAMP)
        .build();

    public static final SomethingHappened SOMETHING_HAPPENED2 = SomethingHappened
        .builder()
        .eventId(EVENT_ID_3)
        .timestamp(TIMESTAMP)
        .addCausality(Causality.of(SOMETHING_HAPPENED))
        .build();

    public static final JSONObject EVENT_DATA_JSON = new JSONObject().put("timestamp", TIMESTAMP);

    public static final String EVENT_DATA = EVENT_DATA_JSON.toString();

    private TestData() {
    }

}
