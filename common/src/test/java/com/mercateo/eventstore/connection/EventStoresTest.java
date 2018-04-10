package com.mercateo.eventstore.connection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.msemys.esjc.EventStore;
import com.mercateo.common.UnitTest;
import com.mercateo.eventstore.config.EventStoreProperties;
import com.mercateo.eventstore.config.EventStorePropertiesCollection;
import com.mercateo.eventstore.domain.EventStoreFailure;
import com.mercateo.eventstore.domain.EventStoreName;

import io.vavr.control.Either;
import lombok.val;

@Category(UnitTest.class)
@RunWith(MockitoJUnitRunner.class)
public class EventStoresTest {

    public static final EventStoreName EVENT_STORE_NAME = EventStoreName.of("foo");

    @Mock
    private EventStoreFactory eventStoreFactory;

    @Mock
    private EventStoreProperties properties;

    @Mock
    private EventStorePropertiesCollection propertiesCollection;

    @Mock
    private EventStore eventstore;

    private EventStores uut;

    @Before
    public void setUp() throws Exception {
        when(eventStoreFactory.createEventStore(properties)).thenReturn(Either.right(eventstore));
        when(properties.getName()).thenReturn(EVENT_STORE_NAME.value());
        when(propertiesCollection.getEventstores()).thenReturn(Collections.singletonList(properties));
        uut = new EventStores(eventStoreFactory, propertiesCollection);
    }

    @Test
    public void returnsEventStoreClient() {
        val result = uut.getEventStore(EVENT_STORE_NAME);

        assertThat(result).contains(eventstore);
    }

    @Test
    public void returnsNoneWhenNameIsUndefined() {
        val result = uut.getEventStore(EventStoreName.of("bar"));

        assertThat(result).isEmpty();
    }

    @Test
    public void cachesEventStoreClient() {
        val result1 = uut.getEventStore(EVENT_STORE_NAME);
        val result2 = uut.getEventStore(EVENT_STORE_NAME);

        verify(eventStoreFactory).createEventStore(properties);
    }

    @Test
    public void doesNotCachesResultsForUnknownName() {
        val throwable = new RuntimeException("problem occured");
        val failure = EventStoreFailure.of(throwable);
        when(eventStoreFactory.createEventStore(properties)).thenReturn(Either.left(failure));

        uut.getEventStore(EVENT_STORE_NAME);
        uut.getEventStore(EVENT_STORE_NAME);

        verify(eventStoreFactory, times(2)).createEventStore(properties);
    }
}