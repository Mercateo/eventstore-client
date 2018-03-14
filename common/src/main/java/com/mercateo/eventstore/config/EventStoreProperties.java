package com.mercateo.eventstore.config;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Validated
public class EventStoreProperties {

    @NotNull
    @Size(min = 1)
    private String name;

    @NotNull
    @Size(min = 1)
    private String host;

    private int port;

    @NotNull
    @Size(min = 1)
    private String username;

    @NotNull
    @Size(min = 1)
    private String password;
}
