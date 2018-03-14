package com.mercateo.eventstore.writer.domain;

import static com.mercateo.eventstore.writer.example.TestData.SOMETHING_HAPPENED;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.mercateo.common.UnitTest;
import com.mercateo.eventstore.domain.Causality;

import lombok.val;

@Category(UnitTest.class)
@RunWith(MockitoJUnitRunner.class)
public class CausalityTest {

    @Test
    public void buildsCausality() throws Exception {

        val cause = SOMETHING_HAPPENED;

        val result = Causality.of(cause);

        assertThat(result.eventId()).isEqualTo(SOMETHING_HAPPENED.eventId());
        assertThat(result.eventType()).isEqualTo(SOMETHING_HAPPENED.eventType());
    }
}
