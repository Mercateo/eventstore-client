package com.mercateo.eventstore.writer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.mercateo.common.UnitTest;
import com.mercateo.eventstore.domain.Causality;
import com.mercateo.eventstore.domain.Event;
import com.mercateo.eventstore.domain.EventId;
import com.mercateo.eventstore.domain.EventInitiator;
import com.mercateo.eventstore.domain.EventSchemaRef;
import com.mercateo.eventstore.domain.EventStoreName;
import com.mercateo.eventstore.domain.EventStreamId;
import com.mercateo.eventstore.domain.EventStreamName;
import com.mercateo.eventstore.domain.EventType;
import com.mercateo.eventstore.domain.EventVersion;
import com.mercateo.eventstore.json.EventJsonMapper;

import io.vavr.Function1;
import io.vavr.collection.List;
import io.vavr.control.Either;
import lombok.val;

interface Event1 extends Event {
}

interface Event2 extends Event {
}

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTest.class)
public class EventStoreEventMapperTest {

    @Mock
    private EventJsonMapper eventJsonMapper;

    @Mock
    private EventMetaDataMapper eventMetaDataMapper;

    private EventStoreEventMapper uut;

    private static final EventStoreName EVENT_STORE_NAME = EventStoreName.of("TEST");

    private static final EventStreamId EVENT_STREAM_ID_1 = EventStreamId.of(EVENT_STORE_NAME, EventStreamName.of(
            "STREAM1"));

    private static final EventStreamId EVENT_STREAM_ID_2 = EventStreamId.of(EVENT_STORE_NAME, EventStreamName.of(
            "STREAM2"));

    private static final EventType EVENT_TYPE_1 = EventType.of("TYPE1");

    public static final Event1 EVENT_1 = new Event1() {
        private EventId eventId = EventId.of(UUID.randomUUID());

        private Instant timestamp = Instant.now();

        @Override
        public EventId eventId() {
            return EventId.of(UUID.randomUUID());
        }

        @Override
        public EventType eventType() {
            return EVENT_TYPE_1;
        }

        @Override
        public Instant timestamp() {
            return timestamp;
        }

        @Override
        public List<Causality> causality() {
            return List.empty();
        }

        @Override
        public Optional<EventInitiator> eventInitiator() {
            return Optional.empty();
        }
    };

    private static final EventType EVENT_TYPE_2 = EventType.of("TYPE2");

    private static final EventVersion EVENT_VERSION_1 = EventVersion.of(1);

    private static final EventVersion EVENT_VERSION_2 = EventVersion.of(2);

    private static final EventSchemaRef EVENT_SCHEMA_REF_1 = ceateSchema("http://test.de/1");

    private static final EventSchemaRef EVENT_SCHEMA_REF_2 = ceateSchema("http://test.de/2");

    private EventConfiguration<Event1> eventConfiguration1;

    private static EventSchemaRef ceateSchema(String uri) {
        EventSchemaRef eventSchemaRef = null;
        try {
            eventSchemaRef = EventSchemaRef.of(new URI(uri));
        } catch (URISyntaxException e) {
            // ignore
        }
        return eventSchemaRef;
    }

    @Before
    public void setUp() throws Exception {
        eventConfiguration1 = new EventConfiguration<Event1>() {
            @Override
            public Function1<Event1, Object> mapper() {
                return event -> Event1Data.builder().timestamp(event.timestamp()).build();
            }

            @Override
            public EventStreamId eventStreamId() {
                return EVENT_STREAM_ID_1;
            }

            @Override
            public EventType getType() {
                return EVENT_TYPE_1;
            }

            @Override
            public EventVersion eventVersion() {
                return EVENT_VERSION_1;
            }

            @Override
            public EventSchemaRef eventSchemaRef() {
                return EVENT_SCHEMA_REF_1;
            }
        };

        EventConfiguration<Event2> eventConfiguration2 = new EventConfiguration<Event2>() {
            @Override
            public Function1<Event2, Object> mapper() {
                return null;
            }

            @Override
            public EventStreamId eventStreamId() {
                return EVENT_STREAM_ID_2;
            }

            @Override
            public EventType getType() {
                return EVENT_TYPE_2;
            }

            @Override
            public EventVersion eventVersion() {
                return EVENT_VERSION_2;
            }

            @Override
            public EventSchemaRef eventSchemaRef() {
                return EVENT_SCHEMA_REF_2;
            }
        };

        uut = new EventStoreEventMapper(Optional.of(Arrays.asList(eventConfiguration1, eventConfiguration2)),
                eventJsonMapper, eventMetaDataMapper);
    }

    @Test
    public void mapsSingleEvent() {
        val eventData1 = eventConfiguration1.mapper().apply(EVENT_1);
        when(eventJsonMapper.toJsonString(eventData1)).thenReturn(Either.right("DATA"));
        when(eventMetaDataMapper.mapMetaData(EVENT_1, eventConfiguration1)).thenReturn(Either.right("METADATA"));

        val result = uut.toEventStoreEvent(Collections.singleton(EVENT_1));

        val eventData1Result = result.get().eventData().iterator().next();
        assertThat(eventData1Result.data).containsExactly("DATA".getBytes());
        assertThat(eventData1Result.metadata).containsExactly("METADATA".getBytes());
    }
}