package com.mercateo.eventstore.config;

import java.util.List;

import lombok.Data;

@Data
public class EventStorePropertiesCollection {

    private List<EventStoreProperties> eventstores;

}
