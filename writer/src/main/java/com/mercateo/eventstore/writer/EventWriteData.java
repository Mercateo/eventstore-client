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
package com.mercateo.eventstore.writer;

import java.util.Collections;

import org.immutables.value.Value;

import com.github.msemys.esjc.EventData;
import com.mercateo.eventstore.domain.EventStreamId;
import com.mercateo.immutables.TupleStyle;

@Value.Immutable
@TupleStyle
public interface EventWriteData {

    static EventWriteData of(EventStreamId eventStreamId, EventData eventData) {
        return ImmutableEventWriteData.of(eventStreamId, Collections.singleton(eventData));
    }

    static EventWriteData of(EventStreamId eventStreamId, Iterable<EventData> eventData) {
        return ImmutableEventWriteData.of(eventStreamId, eventData);
    }

    EventStreamId eventStreamId();

    Iterable<EventData> eventData();
}
