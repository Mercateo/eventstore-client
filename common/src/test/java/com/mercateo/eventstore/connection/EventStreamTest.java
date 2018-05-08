package com.mercateo.eventstore.connection;

import static com.github.msemys.esjc.ExpectedVersion.ANY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.msemys.esjc.EventData;
import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.WriteResult;
import com.mercateo.common.UnitTest;
import com.mercateo.eventstore.domain.EventStoreName;
import com.mercateo.eventstore.domain.EventStreamId;
import com.mercateo.eventstore.domain.EventStreamName;

import lombok.val;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTest.class)
public class EventStreamTest {

    public static final EventStreamId EVENT_STREAM_ID = EventStreamId.of(EventStoreName.of("TestStore"), EventStreamName
        .of("TestStream"));

    @Mock
    private EventStore eventStore;

    @Mock
    private EventData eventData;

    @Mock
    private CompletableFuture<WriteResult> writeResult;

    private EventStream uut;

    @Before
    public void setUp() throws Exception {
        uut = new EventStream(eventStore, EVENT_STREAM_ID);
    }

    @Test
    public void shouldAppendSingleEvent() {
        when(eventStore.appendToStream(EVENT_STREAM_ID.eventStreamName().value(), ANY, eventData)).thenReturn(
                writeResult);

        val result = uut.append(eventData);

        assertThat(result).isEqualTo(writeResult);
    }

    @Test
    public void shouldAppendMultipleEvents() {
        val eventDataIterable = Collections.singleton(eventData);
        when(eventStore.appendToStream(EVENT_STREAM_ID.eventStreamName().value(), ANY, eventDataIterable)).thenReturn(
                writeResult);

        val result = uut.append(eventDataIterable);

        assertThat(result).isEqualTo(writeResult);
    }
}