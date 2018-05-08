package com.mercateo.eventstore.data;

import static org.assertj.core.api.Assertions.assertThat;

import com.mercateo.common.UnitTest;
import com.mercateo.eventstore.example.TestData;
import lombok.val;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ReferenceDataTest {

    @Test
    public void buildsReferenceData() throws Exception {

        val result = ReferenceData.of(TestData.INITIATOR);

        assertThat(result.id()).isEqualTo(TestData.INITIATOR.id());
        assertThat(result.type()).isEqualTo(TestData.INITIATOR.type());
    }
}
