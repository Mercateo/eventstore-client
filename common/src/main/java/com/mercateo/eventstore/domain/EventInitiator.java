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
package com.mercateo.eventstore.domain;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.immutables.value.Value;

import com.mercateo.eventstore.data.EventInitiatorData;
import com.mercateo.eventstore.data.ReferenceData;
import com.mercateo.immutables.DataClass;

@Value.Immutable
@DataClass
public interface EventInitiator extends Reference {

    static ImmutableEventInitiator.Builder builder() {
        return ImmutableEventInitiator.builder();
    }

    @NotNull
    static EventInitiator of(Reference reference) {
        return EventInitiator.builder().id(reference.id()).type(reference.type()).build();
    }

    @NotNull
    static EventInitiator of(EventInitiatorData eventInitiatorData) {
        return EventInitiator
            .builder()
            .id(eventInitiatorData.id())
            .type(eventInitiatorData.type())
            .agent(eventInitiatorData.agent())
            .build();
    }

    @NotNull
    Optional<ReferenceData> agent();
}
