package com.mercateo.eventstore.writer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.mercateo.eventstore.config.EventStoreConfiguration;

@SpringBootApplication
@Import(EventStoreConfiguration.class)
public class TestApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TestApplication.class, args);
    }
}
