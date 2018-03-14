package com.mercateo.eventstore.reader.example;

import org.springframework.stereotype.Component;

import com.mercateo.eventstore.example.SomethingHappened;

@Component
public class SomethingHappenedEventReceiver {

    public void on(@SuppressWarnings("unused") final SomethingHappened event) {
    }

}