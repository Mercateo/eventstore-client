package com.mercateo.eventstore.reader.example;

import com.mercateo.eventstore.domain.EventStreamId;
import com.mercateo.eventstore.example.SomethingHappenedData;
import org.springframework.stereotype.Component;

import com.mercateo.eventstore.domain.EventMetadata;
import com.mercateo.eventstore.domain.EventType;
import com.mercateo.eventstore.example.SomethingHappened;
import com.mercateo.eventstore.reader.EventConsumer;

import lombok.AllArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class SomethingHappenedEventConsumer implements EventConsumer<SomethingHappenedData> {

    private SomethingHappenedEventReceiver consumer;

    @Override
    public EventStreamId eventStreamId() {
        return SomethingHappened.EVENT_STREAM_ID;
    }

    @Override
    public void onEvent(SomethingHappenedData data, EventMetadata metadata) {
        val event = SomethingHappened.builder().eventId(metadata.eventId()).timestamp(data.timestamp()).build();
        log.info("received event {} with metadata {}", event, metadata);
        consumer.on(event);
    }

    @Override
    public Class<? extends SomethingHappenedData> getSerializableDataType() {
        return SomethingHappenedData.class;
    }

    @Override
    public EventType eventType() {
        return SomethingHappened.EVENT_TYPE;
    }

}
