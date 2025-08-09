package com.example.kafka_camel_example.routes;

import com.example.kafka_camel_example.config.KafkaProperties;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProducerRouteTest extends CamelTestSupport {

    private final KafkaProperties kafkaProperties = new KafkaProperties();

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = super.createCamelContext();

        kafkaProperties.setBootstrapServers("localhost:9092");
        kafkaProperties.setTopicName("test-topic");

        return context;
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {

                from("direct:start-test")
                        .routeId("random-number-producer-test")
                        .process(exchange -> {
                            int number = (int) (Math.random() * 101);
                            exchange.getMessage().setBody(number);
                        })
                        .log("Produced random number (test): ${body}")
                        .to("mock:kafka");

                from("direct:start")
                        .routeId("direct-start-producer-test")
                        .process(exchange -> {
                            int number = (int) (Math.random() * 101);
                            exchange.getMessage().setBody(number);
                        })
                        .log("Produced random number (direct test): ${body}")
                        .to("mock:kafka");
            }
        };
    }

    @Test
    public void testRandomNumberProducerRoute() throws Exception {
        MockEndpoint mockKafka = getMockEndpoint("mock:kafka");
        mockKafka.expectedMessageCount(1);

        template.sendBody("direct:start-test", null);

        mockKafka.assertIsSatisfied();

        Integer producedNumber = mockKafka.getExchanges().get(0).getIn().getBody(Integer.class);

        assertNotNull(producedNumber);
        assertTrue(producedNumber >= 0 && producedNumber <= 100);
    }

    @Test
    public void testDirectStartProducerRoute() throws Exception {
        MockEndpoint mockKafka = getMockEndpoint("mock:kafka");
        mockKafka.expectedMessageCount(1);

        template.sendBody("direct:start", null);

        mockKafka.assertIsSatisfied();

        Integer producedNumber = mockKafka.getExchanges().get(0).getIn().getBody(Integer.class);

        assertNotNull(producedNumber);
        assertTrue(producedNumber >= 0 && producedNumber <= 100);
    }
}
