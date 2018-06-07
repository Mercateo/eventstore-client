package com.mercateo.eventstore.example;

import com.mercateo.eventstore.domain.EventInitiator;
import com.mercateo.eventstore.domain.Reference;
import org.json.JSONObject;

import com.github.msemys.esjc.ResolvedEvent;
import com.github.msemys.esjc.proto.EventStoreClientMessages;
import com.github.msemys.esjc.proto.EventStoreClientMessages.EventRecord;
import com.google.protobuf.ByteString;
import com.mercateo.eventstore.domain.Event;

public class TestEventBuilder {

    private JSONObject initiator;

    public static ResolvedEvent buildResolvedEventV1(String streamName, Event event, String eventType,
            JSONObject eventData) {
        EventRecord eventRecord = buildEventRecordV1(streamName, event, eventType, eventData);
        return toResolvedEvent(eventRecord);
    }

    public static ResolvedEvent buildResolvedEventV2(String streamName, Event event, String eventType,
            JSONObject eventData) {
        EventRecord eventRecord = buildEventRecordV2(streamName, event, eventType, eventData);
        return toResolvedEvent(eventRecord);
    }

    public static EventRecord buildEventRecordV1(String streamName, Event event, String eventType,
            JSONObject eventData) {
        String metaData = createLegacyMetaData(event);
        return buildEventRecord(streamName, event, eventType, eventData, metaData);
    }

    public static EventRecord buildEventRecordV2(String streamName, Event event, String eventType,
            JSONObject eventData) {
        String metaData = createMetaData(event);
        return buildEventRecord(streamName, event, eventType, eventData, metaData);
    }

    public static EventRecord buildEventRecord(String streamName, Event event, String eventType, JSONObject eventData,
            String metaData) {
        return EventRecord
            .newBuilder()
            .setEventStreamId(streamName)
            .setEventNumber(1)
            .setEventType(eventType)
            .setEventId(ByteString.copyFromUtf8(event.eventId().value().toString()))
            .setData(ByteString.copyFromUtf8(eventData.toString()))
            .setDataContentType(1)
            .setMetadata(ByteString.copyFromUtf8(metaData))
            .setMetadataContentType(1)
            .build();
    }

    public static ResolvedEvent toResolvedEvent(EventRecord record) {
        return new ResolvedEvent(EventStoreClientMessages.ResolvedEvent
            .newBuilder()
            .setPreparePosition(0)
            .setCommitPosition(0)
            .setEvent(record)
            .build());
    }

    public static String createLegacyMetaData(Event event) {
        return new JSONObject()//
            .put("eventId", event.eventId().value())
            .put("schema", new JSONObject())
            .toString();
    }

    public static String createMetaData(Event event) {

        JSONObject initiator = event
            .eventInitiator()
            .map(eventInitiator -> createReferenceJson(eventInitiator).put("agent", eventInitiator
                .agent()
                .map(TestEventBuilder::createReferenceJson)
                .orElse(null)))
            .orElse(null);

        return new JSONObject()
            .put("eventId", event.eventId().value())
            .put("schemaRef", "https://awesomeschema.org/something-happened_1.json")
            .put("eventType", event.eventType().value())
            .put("version", 1)
            .put("eventInitiator", initiator)
            .toString();
    }

    private static JSONObject createReferenceJson(Reference reference) {
        return new JSONObject().put("id", reference.id()).put("type", reference.type());
    }

}
