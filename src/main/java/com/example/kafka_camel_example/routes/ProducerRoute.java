package com.example.kafka_camel_example.routes;

import com.example.kafka_camel_example.config.KafkaProperties;
import com.example.kafka_camel_example.exception.InvalidNumberRangeException;
import com.example.kafka_camel_example.exception.KafkaConfigurationException;
import com.example.kafka_camel_example.producer.RandomNumberProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class ProducerRoute extends RouteBuilder {

    private final KafkaProperties kafkaProperties;
    private final RandomNumberProducer randomNumberProducer;

    public ProducerRoute(KafkaProperties kafkaProperties) throws KafkaConfigurationException, InvalidNumberRangeException {
        this.kafkaProperties = Objects.requireNonNull(kafkaProperties, "KafkaProperties must not be null");
        validateKafkaProperties(kafkaProperties);

        this.randomNumberProducer = RandomNumberProducer.builder()
                .min(0)
                .max(100)
                .build();
    }

    private void validateKafkaProperties(KafkaProperties props) throws KafkaConfigurationException {
        if (props.getBootstrapServers() == null || props.getBootstrapServers().trim().isEmpty()) {
            throw new KafkaConfigurationException("Bootstrap servers cannot be null or empty");
        }
        if (props.getTopicName() == null || props.getTopicName().trim().isEmpty()) {
            throw new KafkaConfigurationException("Topic name cannot be null or empty");
        }
    }

    @Override
    public void configure() {
        from("timer://producer?period=10000")
                .routeId("random-number-producer")
                .process(exchange -> {
                    try {
                        setRandomNumberBody(exchange);
                    } catch (Exception e) {
                        log.error("Error in setting random number body", e);
                        exchange.setException(e);
                    }
                })
                .log("Produced random number: ${body}")
                .toD(buildKafkaEndpointUri());

        from("direct:start")
                .routeId("direct-start-producer")
                .process(exchange -> {
                    try {
                        setRandomNumberBody(exchange);
                    } catch (Exception e) {
                        log.error("Error in setting random number body (direct)", e);
                        exchange.setException(e);
                    }
                })
                .log("Produced random number (direct): ${body}")
                .toD(buildKafkaEndpointUri());
    }

    private void setRandomNumberBody(Exchange exchange) throws Exception {
        int number = randomNumberProducer.produce();
        exchange.getMessage().setBody(number);
        log.debug("Set random number {} to message body", number);
    }

    private String buildKafkaEndpointUri() {
        return String.format("kafka:%s?brokers=%s",
                kafkaProperties.getTopicName().replaceAll("[^a-zA-Z0-9._-]", "_"),
                kafkaProperties.getBootstrapServers());
    }
}
