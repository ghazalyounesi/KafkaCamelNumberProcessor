package com.example.kafka_camel_example;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@CamelSpringBootTest
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"random-numbers-topic"})
public class KafkaCamelExampleApplicationTests {


    @Autowired
    ProducerTemplate producerTemplate;

    @Test
    void testSendAndConsume() throws Exception {

    }
}
