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
package com.mercateo.eventstore.reader;

import org.immutables.value.Value;

import com.mercateo.eventstore.domain.EventNumber;
import com.mercateo.eventstore.domain.EventStreamId;
import com.mercateo.eventstore.domain.EventType;
import com.mercateo.immutables.TupleStyle;

@Value.Immutable
@TupleStyle
interface StreamMetadata {
    static StreamMetadata of(EventStreamId eventStreamId, EventNumber eventNumber, EventType eventType) {
        return ImmutableStreamMetadata.of(eventStreamId, eventType, eventNumber);
    }

    EventStreamId eventStreamId();

    EventType eventType();

    EventNumber eventNumber();
}
