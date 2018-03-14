package com.mercateo.eventstore.writer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.mercateo.common.IntegrationTest;
import com.mercateo.eventstore.writer.example.TestData;

import lombok.val;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({ "test" })
@Category(IntegrationTest.class)
public class EventStoreWriterIntegrationTest {

    @Autowired
    private EventStoreWriter uut;

    @Test
    public void shouldWriteEvent() throws Exception {

        val result = uut.write(TestData.SOMETHING_HAPPENED);

        assertThat(result.isRight()).isTrue();
    }
}
