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

import static com.mercateo.eventstore.connection.EventStream.BEFORE_FIRST_EVENT;

import com.github.msemys.esjc.CatchUpSubscription;
import com.github.msemys.esjc.CatchUpSubscriptionListener;
import com.github.msemys.esjc.ResolvedEvent;
import com.github.msemys.esjc.SubscriptionDropReason;
import com.github.msemys.esjc.subscription.SubscriptionBufferOverflowException;
import com.mercateo.eventstore.connection.EventStream;
import com.mercateo.eventstore.domain.EventStreamId;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventStreamListener implements CatchUpSubscriptionListener {

    private final EventHandler eventHandler;

    private final EventStream eventStream;

    private final EventStatisticsCollector eventStatisticsCollector;

    private long eventNumber;

    public EventStreamListener(EventHandler eventHandler, EventStream eventStream,
            EventStatisticsCollector eventStatisticsCollector) {
        this.eventHandler = eventHandler;
        this.eventStream = eventStream;
        this.eventStatisticsCollector = eventStatisticsCollector;
    }

    @Override
    public void onEvent(CatchUpSubscription catchUpSubscription, ResolvedEvent resolvedEvent) {
        onEvent(resolvedEvent);
    }

    public void onEvent(ResolvedEvent resolvedEvent) {
        eventNumber = resolvedEvent.event.eventNumber;
        eventHandler.onEvent(resolvedEvent);
        eventStatisticsCollector.onEvent(resolvedEvent.event);
    }

    @Override
    public void onClose(CatchUpSubscription subscription, SubscriptionDropReason reason, Exception exception) {
        log.error("Subscription closed because of {}", reason, exception);
        if ((reason != SubscriptionDropReason.ConnectionClosed && reason != SubscriptionDropReason.UserInitiated)
                || (exception instanceof SubscriptionBufferOverflowException)) {
            log.info("Resubscribing {}", eventStream);

            Try
                .run(() -> eventStream.subscribeToStreamFrom(subscription.lastProcessedEventNumber(), this))
                .onSuccess(ignore -> log.info("resubscription successful"))
                .onFailure(e -> {
                    log.error("resubscription failed", e);
                    setState(EventStreamState.State.REPLAYING);
                });
        }

        if (reason == SubscriptionDropReason.UserInitiated) {
            setState(EventStreamState.State.IDLE);
        } else {
            setState(EventStreamState.State.REPLAYING);
        }
    }

    @Override
    public void onLiveProcessingStarted(CatchUpSubscription subscription) {
        log.info("onLiveProcessingStarted() {}", eventStream.getEventStreamId());
        eventStatisticsCollector.onLiveProcessingStarted();
        setState(EventStreamState.State.LIVE);
    }

    public long getEventNumber() {
        return eventNumber;
    }

    public EventStreamId getEventStreamId() {
        return eventStream.getEventStreamId();
    }

    public void subscribe() {
        subscribe(BEFORE_FIRST_EVENT);
    }

    public void subscribe(long eventNumber) {
        eventStream.subscribeToStreamFrom(eventNumber, this);
        setState(EventStreamState.State.REPLAYING);
    }

    private void setState(EventStreamState.State state) {
        eventStatisticsCollector.getStreamState().setState(state);
    }

    public void stopSubscription() {
        eventStream.stopSubscription();
    }

    public int consumerCountFor(Class<?> clazz) {
        return eventHandler.consumerCountFor(clazz);
    }

}
