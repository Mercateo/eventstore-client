package com.mercateo.eventstore.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.mercateo.common.UnitTest;
import com.mercateo.eventstore.example.TestData;

import lombok.val;

@Category(UnitTest.class)
public class EventInitiatorDataTest {

    @Test
    public void buildsEventInitiatorData() throws Exception {

        val result = EventInitiatorData.of(TestData.EVENT_INITIATOR);

        assertThat(result.id()).isEqualTo(TestData.EVENT_INITIATOR.id());
        assertThat(result.type()).isEqualTo(TestData.EVENT_INITIATOR.type());
        assertThat(result.agent()).isEmpty();
    }

    @Test
    public void buildsEventInitiatorDataWithImpersonator() throws Exception {

        val result = EventInitiatorData.of(TestData.EVENT_INITIATOR_WITH_AGENT);

        assertThat(result.id()).isEqualTo(TestData.EVENT_INITIATOR.id());
        assertThat(result.type()).isEqualTo(TestData.EVENT_INITIATOR.type());
        assertThat(result.agent()).contains(ReferenceData.of(TestData.IMPERSONATOR));
    }
}
