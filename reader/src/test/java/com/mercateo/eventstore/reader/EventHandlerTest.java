package com.mercateo.eventstore.reader;

import static com.mercateo.eventstore.example.SomethingHappened.EVENT_STREAM_ID;
import static com.mercateo.eventstore.example.SomethingHappened.EVENT_TYPE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.msemys.esjc.CatchUpSubscription;
import com.github.msemys.esjc.ResolvedEvent;
import com.github.msemys.esjc.proto.EventStoreClientMessages.EventRecord;
import com.mercateo.common.UnitTest;
import com.mercateo.eventstore.data.SerializableMetadata;
import com.mercateo.eventstore.domain.EventMetadata;
import com.mercateo.eventstore.domain.EventNumber;
import com.mercateo.eventstore.domain.EventStoreFailure;
import com.mercateo.eventstore.example.TestData;
import com.mercateo.eventstore.example.TestEventBuilder;
import com.mercateo.eventstore.json.JsonMapper;

import groovy.json.JsonException;
import io.vavr.collection.List;
import io.vavr.control.Either;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTest.class)
public class EventHandlerTest {
    @Mock
    private EventConsumer<Object> eventConsumer;

    @Mock
    private JsonMapper jsonMapper;

    @Mock
    private MetadataMapper metadataMapper;

    @Mock
    private CatchUpSubscription catchUpSubscription;

    private EventHandler uut;

    @Before
    public void setup() {
        when(eventConsumer.eventStreamId()).thenReturn(EVENT_STREAM_ID);
        when(eventConsumer.eventType()).thenReturn(EVENT_TYPE);
        doReturn(Object.class).when(eventConsumer).getSerializableDataType();
        uut = new EventHandler(List.of(eventConsumer), jsonMapper, metadataMapper);
    }

    @Test
    public void callsEventHandler_onValidEvent() throws Exception {
        Object data = mock(Object.class);
        when(jsonMapper.readValue(TestData.EVENT_STORE_RESOLVED_LEGACY_SOMETHING_HAPPENED_EVENT.event.data,
                Object.class)).thenReturn(Either.right(data));
        SerializableMetadata metadata = mock(SerializableMetadata.class);
        when(jsonMapper.readValue(TestData.EVENT_STORE_RESOLVED_LEGACY_SOMETHING_HAPPENED_EVENT.event.metadata,
                SerializableMetadata.class)).thenReturn(Either.right(metadata));
        EventMetadata eventMetadata = mock(EventMetadata.class);

        when(metadataMapper.mapMetadata(StreamMetadata.of(EVENT_STREAM_ID, EventNumber.of(1L), EVENT_TYPE), metadata))
            .thenReturn(Either.right(eventMetadata));

        uut.onEvent(TestData.EVENT_STORE_RESOLVED_LEGACY_SOMETHING_HAPPENED_EVENT);

        verify(eventConsumer).onEvent(data, eventMetadata);
    }

    @Test
    public void doesNotCallEventHandler_onValidEventOfAnotherType() throws Exception {
        EventRecord record = EventRecord
            .newBuilder(TestData.SOMETHING_HAPPENED_EVENT_RECORD)
            .setEventType("anotherType")
            .build();
        ResolvedEvent resolvedEvent = TestEventBuilder.toResolvedEvent(record);

        uut.onEvent(resolvedEvent);

        verify(eventConsumer, never()).onEvent(any(), any());
    }

    @Test
    public void doesNotCallEventHandler_ifObjectMapperThrowsException() throws Exception {
        when(jsonMapper.readValue(TestData.EVENT_STORE_RESOLVED_LEGACY_SOMETHING_HAPPENED_EVENT.event.data,
                Object.class)).thenReturn(Either.left(EventStoreFailure.of(new JsonException())));
        uut.onEvent(TestData.EVENT_STORE_RESOLVED_LEGACY_SOMETHING_HAPPENED_EVENT);

        verify(eventConsumer, never()).onEvent(any(), any());
    }

    @Test
    public void catchesExceptionsThrownByHandler() throws Exception {
        Object data = mock(Object.class);
        when(jsonMapper.readValue(TestData.EVENT_STORE_RESOLVED_LEGACY_SOMETHING_HAPPENED_EVENT.event.data,
                Object.class)).thenReturn(Either.right(data));
        SerializableMetadata metadata = mock(SerializableMetadata.class);
        when(jsonMapper.readValue(TestData.EVENT_STORE_RESOLVED_LEGACY_SOMETHING_HAPPENED_EVENT.event.metadata,
                SerializableMetadata.class)).thenReturn(Either.right(metadata));
        EventMetadata eventMetadata = mock(EventMetadata.class);
        when(metadataMapper.mapMetadata(StreamMetadata.of(EVENT_STREAM_ID, EventNumber.of(1L), EVENT_TYPE), metadata))
            .thenReturn(Either.right(eventMetadata));

        doThrow(new RuntimeException()).when(eventConsumer).onEvent(data, eventMetadata);

        uut.onEvent(TestData.EVENT_STORE_RESOLVED_LEGACY_SOMETHING_HAPPENED_EVENT);
    }
}
