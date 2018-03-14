package com.mercateo.eventstore.reader;

import static com.github.msemys.esjc.ExpectedVersion.ANY;
import static com.mercateo.eventstore.example.SomethingHappened.EVENT_STREAM_ID;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.msemys.esjc.EventData;
import com.github.msemys.esjc.EventReadResult;
import com.github.msemys.esjc.EventReadStatus;
import com.github.msemys.esjc.EventStore;
import com.mercateo.common.IntegrationTest;
import com.mercateo.eventstore.connection.EventStores;
import com.mercateo.eventstore.example.TestData;
import com.mercateo.eventstore.example.TestEventBuilder;
import com.mercateo.eventstore.reader.example.SomethingHappenedEventReceiver;

import lombok.val;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({ "test" })
@Category(IntegrationTest.class)
public class EventStoreListenerIntegrationTest {

    @Autowired
    EventListeners eventListeners;

    @Autowired
    private EventStores eventStores;

    @MockBean
    private SomethingHappenedEventReceiver consumer;

    private EventStore eventStore;

    private EventStreamListener uut;

    @Before
    public void setUp() throws Exception {
        eventStore = eventStores.getEventStore(EVENT_STREAM_ID.eventStoreName()).get();
        uut = eventListeners.getStreamListener(EVENT_STREAM_ID).get();
    }

    @Test
    public void callsHandlersWhenEventsAreAvailable() throws Exception {

        val event = TestData.SOMETHING_HAPPENED;

        EventReadResult eventReadResult = eventStore.readEvent(EVENT_STREAM_ID.eventStreamName().value(), 0, true).get(
                5, TimeUnit.SECONDS);
        if (eventReadResult.status != EventReadStatus.Success) {
            EventData eventData = EventData
                .newBuilder()
                .type(event.eventType().value())
                .eventId(TestData.SOMETHING_HAPPENED.eventId().value())
                .jsonData(TestData.EVENT_DATA)
                .jsonMetadata(TestEventBuilder.createMetaData(event))
                .build();

            eventStore.appendToStream(EVENT_STREAM_ID.eventStreamName().value(), ANY, eventData).get(5, SECONDS);
        }

        uut.subscribe();

        verify(consumer, timeout(5000)).on(TestData.SOMETHING_HAPPENED);
    }
}
