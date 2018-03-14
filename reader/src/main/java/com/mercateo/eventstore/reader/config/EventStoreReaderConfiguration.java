package com.mercateo.eventstore.reader.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.mercateo.eventstore.config.EventStoreConfiguration;
import com.mercateo.eventstore.reader.EventStoreReaderPackage;

@Configuration
@ComponentScan(basePackageClasses = EventStoreReaderPackage.class)
@Import(EventStoreConfiguration.class)
public class EventStoreReaderConfiguration {
}
