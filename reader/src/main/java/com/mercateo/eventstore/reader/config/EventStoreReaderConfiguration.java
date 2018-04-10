package com.mercateo.eventstore.reader.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.mercateo.eventstore.config.EventStoreConfiguration;
import com.mercateo.eventstore.reader.EventListeners;
import com.mercateo.eventstore.reader.EventSubscriptionHealthIndicator;
import com.mercateo.eventstore.reader.MetadataMapper;

@Configuration
@Import({ EventStoreConfiguration.class, EventListeners.class, MetadataMapper.class,
        EventSubscriptionHealthIndicator.class })
public class EventStoreReaderConfiguration {
}
