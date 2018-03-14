package com.mercateo.eventstore.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.mercateo.common.IntegrationTest;

import lombok.val;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({ "test" })
@Category(IntegrationTest.class)
public class EventStoreConfigurationTest {

    @Autowired
    private EventStorePropertiesCollection propertiesCollection;

    @Test
    public void loadsMultipleEventStoreStreamCollectionPropertiesFromApplicationProperties() {
        final List<EventStoreProperties> list = propertiesCollection.getEventstores();

        assertThat(list).isNotEmpty();
        val firstElement = list.get(0);
        assertThat(firstElement.getHost()).contains("localhost");
    }
}