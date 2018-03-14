package com.mercateo.eventstore.domain;

import javax.validation.constraints.NotNull;
import java.time.Instant;

public interface EventData {

    @NotNull
    Instant timestamp();
}
