package com.mercateo.eventstore.example;

import java.time.Instant;
import java.util.UUID;

import org.json.JSONObject;

import com.github.msemys.esjc.RecordedEvent;
import com.github.msemys.esjc.ResolvedEvent;
import com.github.msemys.esjc.proto.EventStoreClientMessages.EventRecord;
import com.mercateo.eventstore.domain.Causality;
import com.mercateo.eventstore.domain.EventId;
import com.mercateo.eventstore.domain.EventStoreFailure;
import com.mercateo.eventstore.domain.EventStoreFailure.FailureType;
import com.mercateo.eventstore.domain.EventType;
import com.mercateo.eventstore.domain.EventVersion;
import com.mercateo.eventstore.domain.ImmutableEventStoreFailure;

public final class TestData {
    public static final String STREAM_NAME = "test-stream";

    public static final ImmutableEventStoreFailure INTERNAL_ERROR_FAILURE = EventStoreFailure
        .builder()
        .type(FailureType.INTERNAL_ERROR)
        .build();

    public static final UUID EVENT_UUID = UUID.fromString("55555555-5555-5555-5555-555555555551");

    public static final String EVENT_TYPE_STRING = "something-happened";

    public static final EventType EVENT_TYPE = EventType.of(EVENT_TYPE_STRING);

    public static final EventVersion EVENT_VERSION = EventVersion.of(1);

    public static final EventVersion EVENT_VERSION_2 = EventVersion.of(1);

    public static final EventId EVENT_ID = EventId.of(EVENT_UUID);

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

    public static final ResolvedEvent EVENT_STORE_RESOLVED_LEGACY_SOMETHING_HAPPENED_EVENT = TestEventBuilder
        .buildResolvedEvent_v1(STREAM_NAME, SOMETHING_HAPPENED, SomethingHappened.EVENT_TYPE.value(), EVENT_DATA_JSON);

    public static final ResolvedEvent EVENT_STORE_RESOLVED_SOMETHING_HAPPENED_EVENT = TestEventBuilder
        .buildResolvedEvent_v2(STREAM_NAME, SOMETHING_HAPPENED, SomethingHappened.EVENT_TYPE.value(), EVENT_DATA_JSON);

    public static final EventRecord SOMETHING_HAPPENED_EVENT_RECORD = TestEventBuilder.buildEventRecord_v1(STREAM_NAME,
            SOMETHING_HAPPENED, SomethingHappened.EVENT_TYPE.value(), EVENT_DATA_JSON);

    public static final RecordedEvent SOMETHING_HAPPENED_RECORDED_EVENT = EVENT_STORE_RESOLVED_LEGACY_SOMETHING_HAPPENED_EVENT.event;

    public static final String EVENT_METADATA = TestEventBuilder.createLegacyMetaData(SOMETHING_HAPPENED);

    public static final String EVENT_METADATA_2 = TestEventBuilder.createMetaData(SOMETHING_HAPPENED);

    private TestData() {
    }

}
