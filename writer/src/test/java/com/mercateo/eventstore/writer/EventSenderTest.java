package com.mercateo.eventstore.writer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.msemys.esjc.EventData;
import com.github.msemys.esjc.WriteResult;
import com.mercateo.common.UnitTest;
import com.mercateo.eventstore.connection.EventStores;
import com.mercateo.eventstore.connection.EventStream;
import com.mercateo.eventstore.domain.EventStoreFailure;
import com.mercateo.eventstore.domain.EventStoreName;
import com.mercateo.eventstore.domain.EventStreamId;
import com.mercateo.eventstore.domain.EventStreamName;

import io.vavr.control.Either;
import io.vavr.control.Option;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTest.class)
public class EventSenderTest {

    private final EventStreamId eventStreamId = EventStreamId.of(EventStoreName.of("writeEvent-store"), EventStreamName
        .of("writeEvent-stream"));

    private final EventData event = EventData.newBuilder().type("event").build();

    private final EventWriteData writeEvent = EventWriteData.of(eventStreamId, event);

    @Mock
    private EventStores eventstores;

    @Mock
    private EventStream eventStream;

    private EventSender uut;

    @Before
    public void setUp() {
        when(eventstores.getEventStream(eventStreamId)).thenReturn(Option.of(eventStream));

        uut = new EventSender(eventstores);
    }

    @Test
    public void shouldAppendEventToStream() {

        WriteResult writeResult = new WriteResult(0, null);
        CompletableFuture<WriteResult> value = CompletableFuture.completedFuture(writeResult);
        when(eventStream.append(event)).thenReturn(value);

        Either<EventStoreFailure, Void> result = uut.send(writeEvent);

        assertThat(result.get()).isNull();
    }

    @Test
    public void shouldReturnFailure_IfEventStreamThrows() {

        when(eventStream.append(event)).thenThrow(new RuntimeException());

        Either<EventStoreFailure, Void> result = uut.send(writeEvent);

        assertThat(result.getLeft().getType()).isEqualTo(EventStoreFailure.FailureType.INTERNAL_ERROR);
    }
}
