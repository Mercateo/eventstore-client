package com.mercateo.eventstore.data;

import static org.assertj.core.api.Assertions.assertThat;

import com.mercateo.common.UnitTest;
import com.mercateo.eventstore.example.TestData;
import lombok.val;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class EventInitiatorDataTest {

    @Test
    public void buildsEventInitiatorData() throws Exception {

        val result = EventInitiatorData.of(TestData.EVENT_INITIATOR);

        assertThat(result.initiator()).isEqualTo(ReferenceData.of(TestData.EVENT_INITIATOR.initiator()));
        assertThat(result.impersonated()).isEqualTo(null);
    }

    @Test
    public void buildsEventInitiatorDataWithImpersonator() throws Exception {

        val result = EventInitiatorData.of(TestData.EVENT_INITIATOR_WITH_IMPERSONATOR);

        assertThat(result.initiator()).isEqualTo(ReferenceData.of(TestData.EVENT_INITIATOR_WITH_IMPERSONATOR.initiator()));
        assertThat(result.impersonated()).isEqualTo(ReferenceData.of(TestData.EVENT_INITIATOR_WITH_IMPERSONATOR.impersonated().get()));
    }
}
