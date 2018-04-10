package com.mercateo.eventstore.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.mercateo.eventstore.connection.EventStoreFactory;
import com.mercateo.eventstore.connection.EventStores;
import com.mercateo.eventstore.json.EventJsonMapper;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@EnableConfigurationProperties
@Import({ EventStoreFactory.class, EventStores.class, EventJsonMapper.class })
public class EventStoreConfiguration {

    @Bean
    @ConfigurationProperties
    EventStorePropertiesCollection eventStorePropertiesCollection() {
        return new EventStorePropertiesCollection();
    }

}
