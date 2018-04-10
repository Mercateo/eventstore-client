package com.mercateo.eventstore.domain;

import java.time.Instant;

import javax.validation.constraints.NotNull;

public interface EventData {

    @NotNull
    Instant timestamp();
}
