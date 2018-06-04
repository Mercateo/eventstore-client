/**
 * Copyright © 2018 Mercateo AG (http://www.mercateo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mercateo.eventstore.json;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mercateo.eventstore.domain.EventStoreFailure;

import io.vavr.control.Either;
import io.vavr.control.Try;
import io.vavr.jackson.datatype.VavrModule;
import lombok.extern.slf4j.Slf4j;

@Component("eventJsonMapper")
@Slf4j
public class EventJsonMapper {

    private final ObjectMapper objectMapper;

    public EventJsonMapper() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModules(new Jdk8Module(), new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.registerModule(new VavrModule());
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    }

    public Either<EventStoreFailure, String> toJsonString(Object data) {
        return Try //
            .of(() -> objectMapper.writeValueAsString(data))
            .onFailure(e -> log.warn("could not deserialize {}", data != null ? data.getClass().getSimpleName() : null,
                    e))
            .toEither()
            .mapLeft(this::mapInternalError);
    }

    public <E> Either<EventStoreFailure, E> readValue(byte[] rawData, Class<E> dataClass) {
        return Try //
            .of(() -> objectMapper.readValue(rawData, dataClass))
            .toEither()
            .peekLeft(e -> log.warn("could not deserialize {}", dataClass.getSimpleName(), e))
            .mapLeft(this::mapInternalError);
    }

    private EventStoreFailure mapInternalError(Throwable exception) {
        return EventStoreFailure
            .builder()
            .type(EventStoreFailure.FailureType.INTERNAL_ERROR)
            .setValueDataTBD(exception)
            .build();
    }

    public ObjectMapper objectMapper() {
        return objectMapper;
    }
}
