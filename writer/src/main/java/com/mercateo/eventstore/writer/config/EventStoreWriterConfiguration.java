package com.mercateo.eventstore.writer.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.mercateo.eventstore.config.EventStoreConfiguration;
import com.mercateo.eventstore.writer.EventStoreWriterPackage;

import lombok.extern.slf4j.Slf4j;

@Configuration
@ComponentScan(basePackageClasses = EventStoreWriterPackage.class)
@Slf4j
@Import(EventStoreConfiguration.class)
public class EventStoreWriterConfiguration {
}
