package com.mercateo.eventstore.writer.config;

import com.mercateo.eventstore.writer.EventSender;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.mercateo.eventstore.config.EventStoreConfiguration;
import com.mercateo.eventstore.writer.EventMetaDataMapper;
import com.mercateo.eventstore.writer.EventStoreEventMapper;
import com.mercateo.eventstore.writer.EventStoreWriter;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@EnableConfigurationProperties
@Import({ EventStoreConfiguration.class, EventStoreWriter.class, EventStoreEventMapper.class,
        EventMetaDataMapper.class , EventSender.class})
public class EventStoreWriterConfiguration {
}
