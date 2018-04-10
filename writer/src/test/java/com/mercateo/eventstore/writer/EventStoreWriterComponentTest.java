package com.mercateo.eventstore.writer;

import static com.github.msemys.esjc.ExpectedVersion.ANY;
import static com.mercateo.eventstore.example.SomethingHappened.EVENT_STREAM_ID;
import static com.mercateo.eventstore.writer.example.TestData.SOMETHING_HAPPENED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.msemys.esjc.EventData;
import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.Position;
import com.github.msemys.esjc.WriteResult;
import com.mercateo.common.ComponentTest;
import com.mercateo.eventstore.config.EventStoreProperties;
import com.mercateo.eventstore.connection.EventStoreFactory;
import com.mercateo.eventstore.domain.EventStreamName;
import com.mercateo.eventstore.example.SomethingHappenedData;
import com.mercateo.eventstore.json.EventJsonMapper;
import com.mercateo.eventstore.writer.config.EventStoreWriterConfiguration;
import com.mercateo.eventstore.writer.example.SomethingHappenedEventConfiguration;
import com.mercateo.eventstore.writer.example.TestData;

import io.vavr.control.Either;
import lombok.val;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { EventStoreWriterConfiguration.class, SomethingHappenedEventConfiguration.class })
@TestPropertySource(properties = { "eventstores[0].name=default", "eventstores[0].host=127.0.0.1",
        "eventstores[0].port=1113", "eventstores[0].username=admin", "eventstores[0].password=changeit" })
@ActiveProfiles({ "test" })
@Category(ComponentTest.class)
public class EventStoreWriterComponentTest {

    private final static EventStreamName eventStreamName = EVENT_STREAM_ID.eventStreamName();

    @Autowired
    private EventJsonMapper eventJsonMapper;

    @Autowired
    private EventStoreWriter uut;

    @MockBean
    private EventStoreFactory eventstoreFactory;

    @Mock
    private EventStore eventStore;

    @Captor
    private ArgumentCaptor<EventData> argument = ArgumentCaptor.forClass(EventData.class);

    private JacksonTester<SomethingHappenedData> dataJson;

    @Before
    public void setUp() {
        JacksonTester.initFields(this, eventJsonMapper.objectMapper());
    }

    @Test
    public void shouldWriteEvent() throws IOException {

        val writeResult = CompletableFuture.completedFuture(new WriteResult(0, Position.START));

        val eventStoreProperties = new EventStoreProperties();
        eventStoreProperties.setName(EVENT_STREAM_ID.eventStoreName().value());
        eventStoreProperties.setHost("127.0.0.1");
        eventStoreProperties.setPort(1113);
        eventStoreProperties.setUsername("admin");
        eventStoreProperties.setPassword("changeit");

        when(eventstoreFactory.createEventStore(eventStoreProperties)).thenReturn(Either.right(eventStore));

        when(eventStore.appendToStream(eq(eventStreamName.value()), eq(ANY), any(EventData.class))).thenReturn(
                writeResult);

        val result = uut.write(SOMETHING_HAPPENED);

        assertThat(result.isRight()).isTrue();

        verify(eventStore).appendToStream(eq(eventStreamName.value()), eq(ANY), argument.capture());
        EventData actualEvent = argument.getValue();

        assertThat(actualEvent.type).isEqualTo("something-happened");
        assertThat(actualEvent.eventId).isEqualTo(SOMETHING_HAPPENED.eventId().value());
        assertThat(dataJson.parse(actualEvent.data)).isEqualToComparingFieldByFieldRecursively(dataJson
            .parse(TestData.EVENT_DATA)
            .getObject());
    }

}