package com.example.kafka_camel_example.routes;

import com.example.kafka_camel_example.service.AccumulatorService;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@CamelSpringBootTest
@SpringBootTest(properties = {
        "kafka.bootstrapServers=localhost:9092",
        "kafka.topicName=test-topic",
        "app.useKafka=false"
})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class ConsumerRouteTest {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private AccumulatorService accumulatorService;

    @Autowired
    private CamelContext camelContext;

    @Test
    void contextLoads() {
        assertNotNull(camelContext);
    }

    @Test
    void testNumberConsumptionAndAccumulation() throws Exception {
        accumulatorService.getAndReset();

        producerTemplate.sendBody("direct:testInput", 10);
        producerTemplate.sendBody("direct:testInput", 20);

        long sum = waitForSum(30, 2000);
        assertEquals(30, sum);
    }

    private long waitForSum(long expected, long timeoutMs) throws InterruptedException {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeoutMs) {
            long current = accumulatorService.getAndReset();
            if (current == expected) {
                return current;
            }
            Thread.sleep(50);
        }
        fail("Timed out waiting for sum " + expected);
        return -1;
    }


    @Test
    void testAccumulatorReset() throws Exception {
        accumulatorService.getAndReset();

        producerTemplate.sendBody("direct:testInput", 5);
        producerTemplate.sendBody("direct:testInput", 15);

        Thread.sleep(500);

        long sum1 = accumulatorService.getAndReset();
        assertEquals(20, sum1);

        long sum2 = accumulatorService.getAndReset();
        assertEquals(0, sum2);
    }


}
