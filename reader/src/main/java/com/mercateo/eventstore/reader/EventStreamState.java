package com.mercateo.eventstore.reader;

import com.mercateo.eventstore.domain.EventStreamId;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventStreamState {

    private final EventStreamId streamId;

    private State state;

    public EventStreamState(EventStreamId streamId) {
        this.streamId = streamId;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        if (state != this.state) {
            log.info("state transition for {}: {} -> {}", streamId, this.state, state);
        }

        this.state = state;
    }

    public EventStreamId getStreamId() {
        return streamId;
    }

    public enum State {
        REPLAYING, LIVE, IDLE
    }
}
