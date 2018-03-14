package com.mercateo.eventstore.reader;

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
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.msemys.esjc.EventData;
import com.github.msemys.esjc.EventReadResult;
import com.github.msemys.esjc.EventReadStatus;
import com.mercateo.common.IntegrationTest;
import com.mercateo.eventstore.connection.EventStores;
import com.mercateo.eventstore.connection.EventStream;
import com.mercateo.eventstore.example.SomethingHappened;
import com.mercateo.eventstore.example.TestData;
import com.mercateo.eventstore.example.TestEventBuilder;
import com.mercateo.eventstore.reader.example.SomethingHappenedEventReceiver;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({ "test" })
@Category(IntegrationTest.class)
@Slf4j
public class EventStoreListenerRecordedDataHandlerIntegrationTest {

    @Autowired
    private EventStores eventStores;

    @Autowired
    private EventListeners eventListeners;

    @SpyBean
    private SomethingHappenedEventReceiver consumer;

    private EventStream eventStream;

    @Before
    public void setUp() throws Exception {
        eventStream = eventStores
            .getEventStream(SomethingHappened.EVENT_STREAM_ID)
            .get();
    }

    @Test
    public void callsHandlersWhenEventsAreAvailable() throws Exception {
        val streamListener = eventListeners
            .getStreamListener(SomethingHappened.EVENT_STREAM_ID)
            .get();

        val event = TestData.SOMETHING_HAPPENED;
        EventReadResult eventReadResult = eventStream.readEvent(0, true).get(5, TimeUnit.SECONDS);
        if (eventReadResult.status != EventReadStatus.Success) {

            val eventData = EventData
                .newBuilder()
                .type(SomethingHappened.EVENT_TYPE.value())
                .eventId(event.eventId().value())
                .jsonData(TestData.EVENT_DATA)
                .jsonMetadata(TestEventBuilder.createMetaData(event))
                .build();

            log.info("add test event to eventStreamName");
            eventStream.append(eventData).get(5, SECONDS);
        }

        streamListener.subscribe();

        verify(consumer, timeout(10000)).on(event);
    }
}
