package com.mercateo.eventstore.reader;

import static com.mercateo.eventstore.example.SomethingHappened.EVENT_SCHEMA_REF;
import static com.mercateo.eventstore.reader.EventMetadataMapper.LEGACY_VERSION;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.mercateo.common.UnitTest;
import com.mercateo.eventstore.data.CausalityData;
import com.mercateo.eventstore.data.EventInitiatorData;
import com.mercateo.eventstore.data.SerializableMetadata;
import com.mercateo.eventstore.domain.Causality;
import com.mercateo.eventstore.domain.EventId;
import com.mercateo.eventstore.domain.EventInitiator;
import com.mercateo.eventstore.domain.EventNumber;
import com.mercateo.eventstore.domain.EventStoreFailure;
import com.mercateo.eventstore.domain.EventStoreName;
import com.mercateo.eventstore.domain.EventStreamId;
import com.mercateo.eventstore.domain.EventStreamName;
import com.mercateo.eventstore.domain.EventType;
import com.mercateo.eventstore.domain.EventVersion;
import com.mercateo.eventstore.example.TestData;

import lombok.val;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTest.class)
public class EventMetadataMapperTest {

    private EventMetadataMapper uut;

    private EventType eventType;

    private EventNumber eventNumber;

    private StreamMetadata streamMetadata;

    private EventId eventId;

    private EventVersion eventVersion;

    private EventStreamId eventStreamId;

    @Before
    public void setUp() throws Exception {
        uut = new EventMetadataMapper();

        val eventStoreName = EventStoreName.of("test-store");
        val eventStreamName = EventStreamName.of("test-eventStreamName");
        eventStreamId = EventStreamId.of(eventStoreName, eventStreamName);
        eventType = EventType.of("something-happened");
        eventNumber = EventNumber.of(123l);
        streamMetadata = StreamMetadata.of(eventStreamId, eventNumber, eventType);
        eventId = EventId.of(UUID.randomUUID());
        eventVersion = EventVersion.of(5);
    }

    @Test
    public void mapsLegacyMetadata() {
        val serializableMetadata = SerializableMetadata.builder().eventId(eventId.value()).build();

        val result = uut.mapMetadata(streamMetadata, serializableMetadata).get();

        assertThat(result.eventId()).isEqualTo(eventId);
        assertThat(result.eventType()).isEqualTo(eventType);
        assertThat(result.eventNumber()).isEqualTo(eventNumber);
        assertThat(result.eventStreamId()).isEqualTo(eventStreamId);
        assertThat(result.causality()).isEmpty();
        assertThat(result.eventInitiator()).isEmpty();
        assertThat(result.version()).isEqualTo(LEGACY_VERSION);
    }

    @Test
    public void mapsMetadata() {
        val serializableMetadata = SerializableMetadata
            .builder()
            .eventId(eventId.value())
            .version(eventVersion.value())
            .schemaRef(EVENT_SCHEMA_REF.value().toString())
            .build();

        val result = uut.mapMetadata(streamMetadata, serializableMetadata).get();

        assertThat(result.eventId()).isEqualTo(eventId);
        assertThat(result.eventType()).isEqualTo(eventType);
        assertThat(result.eventNumber()).isEqualTo(eventNumber);
        assertThat(result.eventStreamId()).isEqualTo(eventStreamId);
        assertThat(result.causality()).isEmpty();
        assertThat(result.eventInitiator()).isEmpty();
        assertThat(result.version()).isEqualTo(eventVersion);
        assertThat(result.eventSchemaRef()).contains(EVENT_SCHEMA_REF);
    }

    @Test
    public void mapsCausalities() {
        val causality1 = Causality
            .builder()
            .eventId(EventId.of(UUID.randomUUID()))
            .eventType(EventType.of("foo"))
            .build();

        val causality2 = Causality
            .builder()
            .eventId(EventId.of(UUID.randomUUID()))
            .eventType(EventType.of("bar"))
            .build();

        val serializableMetadata = SerializableMetadata
            .builder()
            .eventId(eventId.value())
            .version(eventVersion.value())
            .schemaRef(EVENT_SCHEMA_REF.value().toString())
            .causality(CausalityData.of(causality1), CausalityData.of(causality2))
            .build();

        val result = uut.mapMetadata(streamMetadata, serializableMetadata).get();

        assertThat(result.causality()).containsExactlyInAnyOrder(causality1, causality2);
    }

    @Test
    public void mapsEventInitiator() {
        val eventInitiator = EventInitiator.builder().initiator(TestData.INITIATOR).build();

        val serializableMetadata = SerializableMetadata
            .builder()
            .eventId(eventId.value())
            .version(eventVersion.value())
            .schemaRef(EVENT_SCHEMA_REF.value().toString())
            .eventInitiator(EventInitiatorData.of(eventInitiator))
            .build();

        val result = uut.mapMetadata(streamMetadata, serializableMetadata).get();

        assertThat(result.eventInitiator()).containsExactlyInAnyOrder(eventInitiator);

    }

    @Test
    public void mapsEventInitiatorWithImposter() {
        val eventInitiatorWithImposter = EventInitiator
            .builder()
            .initiator(TestData.INITIATOR)
            .setValueImpersonated(TestData.IMPERSONATOR)
            .build();

        val serializableMetadata = SerializableMetadata
            .builder()
            .eventId(eventId.value())
            .version(eventVersion.value())
            .schemaRef(EVENT_SCHEMA_REF.value().toString())
            .eventInitiator(EventInitiatorData.of(eventInitiatorWithImposter))
            .build();

        val result = uut.mapMetadata(streamMetadata, serializableMetadata).get();

        assertThat(result.eventInitiator()).containsExactlyInAnyOrder(eventInitiatorWithImposter);

    }

    @Test
    public void failsOnMissingSchemaRef() {
        val streamMetadata = StreamMetadata.of(eventStreamId, eventNumber, eventType);
        val eventId = EventId.of(UUID.randomUUID());
        val eventVersion = EventVersion.of(5);
        val serializableMetadata = SerializableMetadata
            .builder()
            .eventId(eventId.value())
            .version(eventVersion.value())
            .build();

        val result = uut.mapMetadata(streamMetadata, serializableMetadata).getLeft();

        assertThat(result.getType()).isEqualTo(EventStoreFailure.FailureType.INTERNAL_ERROR);
    }
}