package com.mercateo.eventstore.connection;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.mercateo.common.UnitTest;
import com.mercateo.eventstore.config.EventStoreProperties;

import lombok.val;

@Category(UnitTest.class)
public class EventStoreFactoryTest {

    @Test
    public void createsEventStore() {
        final EventStoreFactory uut = new EventStoreFactory();
        final EventStoreProperties properties = new EventStoreProperties();
        properties.setName("foo");
        properties.setHost("localhost");
        properties.setPort(1113);
        properties.setUsername("admin");
        properties.setPassword("changeit");

        val result = uut.createEventStore(properties);

        assertThat(result).isNotEmpty();
    }

    @Test
    public void handlesFailureDuringCreation() {
        final EventStoreFactory uut = new EventStoreFactory();
        final EventStoreProperties properties = new EventStoreProperties();

        val result = uut.createEventStore(properties);

        assertThat(result).isEmpty();
    }
}