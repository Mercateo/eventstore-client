package com.mercateo.eventstore.reader;

import static com.mercateo.eventstore.reader.EventStreamState.State.REPLAYING;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component("subscriptionHealthIndicator")
@Slf4j
public class SubscriptionHealthIndicator extends AbstractHealthIndicator {

    private final Set<EventStatisticsCollector> activeMetrics;

    public SubscriptionHealthIndicator() {
        this.activeMetrics = new HashSet<>();
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        if (isHealty()) {
            builder.up();
        } else {
            log.warn("eventstore subscription not healthy on {}", unhealthySubscriptions());
            builder.down();
        }
    }

    public boolean isHealty() {
        return activeMetrics.stream().allMatch(metrics -> metrics.getState() != REPLAYING);
    }

    private String unhealthySubscriptions() {
        return activeMetrics
            .stream()
            .filter(metrics -> metrics.getState() == REPLAYING)
            .map(EventStatisticsCollector::getEventStreamId)
            .map(Object::toString)
            .collect(Collectors.joining(", "));
    }

    public void addToMonitoring(EventStatisticsCollector eventStatisticsCollector) {
        activeMetrics.add(eventStatisticsCollector);
    }
}
