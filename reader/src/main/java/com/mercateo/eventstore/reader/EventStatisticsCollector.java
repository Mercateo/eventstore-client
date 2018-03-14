package com.mercateo.eventstore.reader;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicLong;

import com.github.msemys.esjc.RecordedEvent;
import com.mercateo.eventstore.connection.EventStream;
import com.mercateo.eventstore.domain.EventStreamId;

import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventStatisticsCollector {

    private final EventStreamState state;

    private final Option<EventMetrics> client;

    private long startTime = System.currentTimeMillis();

    private AtomicLong eventCount = new AtomicLong();

    private AtomicLong totalSize = new AtomicLong();

    private boolean inInitialReplay = true;

    private final EventStreamId eventStreamId;

    public EventStatisticsCollector(EventStream eventStream, Option<EventMetrics> client) {
        eventStreamId = eventStream.getEventStreamId();
        this.state = new EventStreamState(eventStreamId);
        this.client = client;
        log.info("use metrics client: {}", this.client.map(ignore -> "YES").getOrElse(() -> "NO"));
    }

    public void onLiveProcessingStarted() {
        if (inInitialReplay) {
            log.info("replayed {} events with {} bytes in {} s", eventCount.get(), totalSize.get(),
                    getElapsedTimeInSeconds());

            client.forEach(c -> c.onReplayFinished(eventStreamId, getEventCount(), getEventRate(), getEventDataRate()));

            inInitialReplay = false;
        }
    }

    public void onEvent(RecordedEvent event) {
        if (event == null) {
            log.warn("onEvent() received null event");
            return;
        }

        final long eventCount = this.eventCount.incrementAndGet();

        final String renderedEvent = new String(event.data, Charset.defaultCharset());
        if (!inInitialReplay) {
            log.info("event {} received with type {}: {}", eventCount, event.eventType, renderedEvent);
        } else {
            log.trace("event {} received with type {}: {}", eventCount, event.eventType, renderedEvent);
        }

        totalSize.addAndGet(event.data.length + event.metadata.length);
        client.forEach(c -> c.onEvent(eventStreamId));
    }

    protected long getEventCount() {
        return eventCount.get();
    }

    protected long getTotalSize() {
        return totalSize.get();
    }

    private double getElapsedTimeInSeconds() {
        return (System.currentTimeMillis() - startTime) / 1000.0;
    }

    protected double getEventRate() {
        double elapsedTimeInSeconds = getElapsedTimeInSeconds();
        return getEventCount() / elapsedTimeInSeconds;
    }

    protected double getEventDataRate() {
        double elapsedTimeInSeconds = getElapsedTimeInSeconds();
        return getTotalSize() / elapsedTimeInSeconds;
    }

    public EventStreamState.State getState() {
        return state.getState();
    }

    public EventStreamId getEventStreamId() {
        return state.getStreamId();
    }
}
