package com.mercateo.eventstore.writer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.mercateo.common.UnitTest;
import com.mercateo.eventstore.example.SomethingHappened;
import com.mercateo.eventstore.writer.example.TestData;

import io.vavr.control.Either;
import lombok.val;

import java.util.Collections;

@Category(UnitTest.class)
@RunWith(MockitoJUnitRunner.class)
public class EventStoreWriterTest {

    @Mock
    private SomethingHappened domainEvent;

    @Mock
    private EventWriteData eventStoreData;

    @Mock
    private EventStoreEventMapper eventMapper;

    @Mock
    private EventSender eventSender;

    @InjectMocks
    private EventStoreWriter uut;

    @Test
    public void writesEventsSuccesfully() throws Exception {
        when(eventMapper.toEventStoreEvent(Collections.singleton(domainEvent))).thenReturn(Either.right(eventStoreData));
        when(eventSender.send(eventStoreData)).thenReturn(Either.right(null));
        val savingResult = uut.write(domainEvent);
        assertThat(savingResult.get()).isEqualTo(domainEvent);
    }

    @Test
    public void failsOnEventDataCreation() throws Exception {
        when(eventMapper.toEventStoreEvent(Collections.singleton(domainEvent))).thenReturn(Either.left(TestData.INTERNAL_ERROR_FAILURE));
        val result = uut.write(domainEvent);
        assertThat(result.getLeft()).isSameAs(TestData.INTERNAL_ERROR_FAILURE);
        verifyNoMoreInteractions(eventSender);

    }

    @Test
    public void failsOnSave() throws Exception {
        when(eventMapper.toEventStoreEvent(Collections.singleton(domainEvent))).thenReturn(Either.right(eventStoreData));
        when(eventSender.send(eventStoreData)).thenReturn(Either.left(TestData.INTERNAL_ERROR_FAILURE));
        val result = uut.write(domainEvent);
        assertThat(result.getLeft()).isSameAs(TestData.INTERNAL_ERROR_FAILURE);
    }
}
