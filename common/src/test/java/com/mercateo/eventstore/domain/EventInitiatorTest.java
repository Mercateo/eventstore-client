package com.mercateo.eventstore.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Optional;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.mercateo.common.UnitTest;
import com.mercateo.eventstore.data.EventInitiatorData;
import com.mercateo.eventstore.data.ImmutableEventInitiatorData;
import com.mercateo.eventstore.data.ReferenceData;
import com.mercateo.eventstore.example.TestData;

import lombok.val;

@Category(UnitTest.class)
public class EventInitiatorTest {

    @Test
    public void mapsInitiator() {
        Reference reference = TestData.EVENT_INITIATOR_WITH_AGENT;

        val initiator = EventInitiator.of(reference);

        assertThat(initiator.id()).isEqualTo(reference.id());
        assertThat(initiator.type()).isEqualTo(reference.type());
        assertThat(initiator.agent()).isEmpty();

    }

    @Test
    public void mapsInitiatorData() {
        val initiatorData = EventInitiatorData.of(TestData.EVENT_INITIATOR_WITH_AGENT);

        val initiator = EventInitiator.of(initiatorData);

        assertThat(initiator.id()).isEqualTo(initiatorData.id());
        assertThat(initiator.type()).isEqualTo(initiatorData.type());
        assertThat(initiator.agent()).contains(initiatorData.agent().get(0));
    }

    @Test
    public void mapsLegacyInitiatorData() {
        val initiator = TestData.INITIATOR;
        val initiatorData = ImmutableEventInitiatorData
            .builder()
            .initiator(ReferenceData.of(initiator))
            .build();

        val result = EventInitiator.of(initiatorData);

        assertThat(result.id()).isEqualTo(initiator.id());
        assertThat(result.type()).isEqualTo(initiator.type());
        assertThat(result.agent()).isEmpty();
    }
}