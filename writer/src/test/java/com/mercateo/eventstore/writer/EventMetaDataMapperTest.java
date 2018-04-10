package com.mercateo.eventstore.writer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.json.JacksonTester;

import com.mercateo.common.UnitTest;
import com.mercateo.eventstore.data.CausalityData;
import com.mercateo.eventstore.data.SerializableMetadata;
import com.mercateo.eventstore.domain.Causality;
import com.mercateo.eventstore.domain.EventId;
import com.mercateo.eventstore.domain.EventType;
import com.mercateo.eventstore.example.SomethingHappened;
import com.mercateo.eventstore.json.EventJsonMapper;
import com.mercateo.eventstore.writer.example.TestData;

import lombok.val;

@Category(UnitTest.class)
@RunWith(MockitoJUnitRunner.class)
public class EventMetaDataMapperTest {

    private static final SomethingHappened EVENT = SomethingHappened
        .builder()
        .eventId(TestData.EVENT_ID)
        .timestamp(TestData.TIMESTAMP)
        .build();

    private static final String CAUSING_EVENT_TYPE = "causing-event-type";

    private static final EventId CAUSE_EVENT_ID = EventId.of(UUID.fromString("55555555-5555-5555-5555-555555555554"));

    private JacksonTester<SerializableMetadata> metadataJson;

    @Mock
    private EventConfiguration mapper;

    private EventMetaDataMapper uut;

    @Before
    public void setUp() throws Exception {
        val jsonMapper = new EventJsonMapper();
        JacksonTester.initFields(this, jsonMapper.objectMapper());
        uut = new EventMetaDataMapper(jsonMapper);

        when(mapper.eventSchemaRef()).thenReturn(TestData.EVENT_SCHEMA_REF);
        when(mapper.eventVersion()).thenReturn(TestData.EVENT_VERSION);
    }

    @Test
    public void containsEventId() throws Exception {

        val result = uut.mapMetaData(EVENT, mapper);

        assertThat(result).isNotEmpty();
        assertThat(metadataJson.parse(result.get())).hasFieldOrPropertyWithValue("eventId", UUID.fromString(
                "55555555-5555-5555-5555-555555555551"));
    }

    @Test
    public void containsSchemaRef() throws Exception {

        val result = uut.mapMetaData(EVENT, mapper);

        assertThat(result).isNotEmpty();
        assertThat(metadataJson.parse(result.get())).hasFieldOrPropertyWithValue("schemaRef",
                TestData.EVENT_SCHEMA_REF_STRING);
    }

    @Test
    public void containsVersion() throws Exception {

        val result = uut.mapMetaData(EVENT, mapper);

        assertThat(result).isNotEmpty();
        assertThat(metadataJson.parse(result.get())).hasFieldOrPropertyWithValue("version", TestData.EVENT_VERSION
            .value());
    }

    @Test
    public void containsEventType() throws Exception {

        val result = uut.mapMetaData(EVENT, mapper);

        assertThat(result).isNotEmpty();
        assertThat(metadataJson.parse(result.get())).hasFieldOrPropertyWithValue("eventType", TestData.EVENT_TYPE
            .value());
    }

    @Test
    public void containsCausality() throws Exception {

        Causality causality = Causality
            .builder()
            .eventId(CAUSE_EVENT_ID)
            .eventType(EventType.of(CAUSING_EVENT_TYPE))
            .build();

        val event = SomethingHappened.builder().from(EVENT).addCausality(causality).build();

        val result = uut.mapMetaData(event, mapper);

        assertThat(result).isNotEmpty();
        assertThat(metadataJson.parse(result.get())).hasFieldOrPropertyWithValue("causality", new CausalityData[] {
                CausalityData.of(causality) });
    }

    @Test
    public void doesNotContainCausality() throws Exception {
        val result = uut.mapMetaData(EVENT, mapper);
        assertThat(result).doesNotContain("causality");
    }
}
