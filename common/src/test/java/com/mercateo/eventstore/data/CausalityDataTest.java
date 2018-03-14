package com.mercateo.eventstore.data;

import static com.mercateo.eventstore.example.TestData.SOMETHING_HAPPENED;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.mercateo.common.UnitTest;
import com.mercateo.eventstore.domain.Causality;

import lombok.val;

@Category(UnitTest.class)
public class CausalityDataTest {
    @Test
    public void buildsCausalityData() throws Exception {

        val causality = Causality.of(SOMETHING_HAPPENED);

        val result = CausalityData.of(causality);

        assertThat(result.eventId()).isEqualTo(SOMETHING_HAPPENED.eventId().value());
        assertThat(result.eventType()).isEqualTo(SOMETHING_HAPPENED.eventType().value());
    }
}