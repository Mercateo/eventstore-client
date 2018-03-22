[![Build Status](https://travis-ci.org/Mercateo/eventstore-client.svg?branch=master)](https://travis-ci.org/Mercateo/eventstore-client)
[![Coverage Status](https://coveralls.io/repos/github/Mercateo/eventstore-client/badge.svg?branch=master)](https://coveralls.io/github/Mercateo/eventstore-client?branch=master)
[![MavenCentral](https://img.shields.io/maven-central/v/com.mercateo.eventstore/parent.svg)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.mercateo.eventstore%22%20AND%20a%3A%22client-reader%22)


# Eventstore Client

The official Event Store page can be found at https://eventstore.org/

This project uses the Java 8 client for the EventStore available at https://github.com/msemys/esjc

Our adapter implementation is generic and can be used for any kind of event. Trivial mapping between domain objects and json-serializable objects should be implemented, if the domain objects themselves can not be serialized and deserialized.

In order to consume all events from a stream, method `EventListeners.subscribeStream()` should be called. There is a variant `EventListenerssubscribeStreamStartingAt(...)` when the stream should be consumed starting from a particular event number.
The client ensures to reconnect to the eventstore after being disconnected for any reason.

The known eventstores for the client are configured by setting application properties as in the following (YML) example:

```yaml
eventstores:
  - name: eventstore-team-a
    host: eventstore.host.name
    port: 1113
    username: admin
    password: changeit
```

## Usage example

### Common

#### Event

```java
@Value.Immutable
@DataClass
public interface SomethingHappened extends Event {

    EventStreamId EVENT_STREAM_ID = EventStreamId.of(EventStoreName.of("default"), EventStreamName.of("test"));

    EventType EVENT_TYPE = EventType.of("something-happened");

    EventVersion EVENT_VERSION = EventVersion.of(3);

    EventSchemaRef EVENT_SCHEMA_REF = EventSchemaRef.of(URI.create("https://test.com/ref"));

    static ImmutableSomethingHappened.Builder builder() {
        return ImmutableSomethingHappened.builder();
    }

    String foo();
    
    Integer bar();
    
    @Override
    default EventType eventType() {
        return EVENT_TYPE;
    }
}
```

#### Serializable Event

```java
@Value.Immutable
@DataClass
@JsonSerialize(as = ImmutableSomethingHappenedData.class)
@JsonDeserialize(as = ImmutableSomethingHappenedData.class)
public interface SomethingHappenedData extends EventData {

    static ImmutableSomethingHappenedData.Builder builder() {
        return ImmutableSomethingHappenedData.builder();
    }
    
    String foo();
    
    Integer bar();
}
```

### For consuming events of a particular stream

```java
@Component
@AllArgsConstructor
@Slf4j
public class SomethingHappenedEventConsumer implements EventConsumer<SomethingHappenedData> {
    
    @Override
    public EventStreamId eventStreamId() {
        return SomethingHappened.EVENT_STREAM_ID;
    }

    @Override
    public void onEvent(SomethingHappenedData data, EventMetadata metadata) {
        val event = SomethingHappened.builder()
                .eventId(metadata.eventId())
                .timestamp(data.timestamp())
                .foo(data.foo())
                .bar(data.bar)
                .build();
        
        log.info("received event {} with metadata {}", event, metadata);
        
        // code to consume event should be placed here
    }

    @Override
    public Class<? extends SomethingHappenedData> getSerializableDataType() {
        return SomethingHappenedData.class;
    }

    @Override
    public EventType eventType() {
        return SomethingHappened.EVENT_TYPE;
    }

}
```

### For writing events to a particular stream

```java
@Component
class SomethingHappenedEventConfiguration implements EventConfiguration<SomethingHappened> {

    public SomethingHappenedData map(SomethingHappened somethingHappened) {
        return SomethingHappenedData.builder()
                .timestamp(somethingHappened.timestamp())
                .foo(somethingHappened.foo())
                .bar(somethingHappened.bar())
                .build();
    }

    @Override
    public EventStreamId eventStreamId() {
        return EVENT_STREAM_ID;
    }

    @Override
    public EventType getType() {
        return EVENT_TYPE;
    }

    @Override
    public EventVersion eventVersion() {
        return EVENT_VERSION;
    }

    @Override
    public EventSchemaRef eventSchemaRef() {
        return EVENT_SCHEMA_REF;
    }

    @Override
    public Function1<SomethingHappened, Object> mapper() {
        return this::map;
    }

}
```


