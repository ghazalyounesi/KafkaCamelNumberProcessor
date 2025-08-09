package com.example.kafka_camel_example.routes;

import com.example.kafka_camel_example.config.KafkaProperties;
import com.example.kafka_camel_example.exception.InvalidNumberFormatException;
import com.example.kafka_camel_example.service.AccumulatorService;
import com.example.kafka_camel_example.util.NumberExtractor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConsumerRoute extends RouteBuilder {

    private final KafkaProperties kafkaProperties;
    private final AccumulatorService accumulatorService;

    @Value("${app.useKafka:true}")
    private boolean useKafka;

    public ConsumerRoute(@NonNull KafkaProperties kafkaProperties, @NonNull AccumulatorService accumulatorService) {
        this.kafkaProperties = kafkaProperties;
        this.accumulatorService = accumulatorService;
    }

    public static ConsumerRoute create(@NonNull KafkaProperties kafkaProperties, @NonNull AccumulatorService accumulatorService) {
        return new ConsumerRoute(kafkaProperties, accumulatorService);
    }

    @Override
    public void configure() {

        if (useKafka) {
            from("kafka:" + kafkaProperties.getTopicName() +
                    "?brokers=" + kafkaProperties.getBootstrapServers() +
                    "&groupId=camel-consumer")
                    .routeId("random-number-consumer")
                    .process(exchange -> {
                        Object body = exchange.getMessage().getBody();
                        try {
                            Long number = NumberExtractor.extractNumber(body);
                            if (number != null) {
                                accumulatorService.add(number);
                                log.debug("Consumed number: {}", number);
                            } else {
                                log.warn("Received null or invalid number: {}", body);
                            }
                        } catch (InvalidNumberFormatException e) {
                            log.warn("Invalid number format exception: {}", e.getMessage());
                            exchange.setException(e);
                        }
                    });
        } else {
            from("direct:testInput")
                    .routeId("random-number-consumer-test")
                    .process(exchange -> {
                        Object body = exchange.getMessage().getBody();
                        try {
                            Long number = NumberExtractor.extractNumber(body);
                            if (number != null) {
                                accumulatorService.add(number);
                                log.debug("Consumed number: {}", number);
                            } else {
                                log.warn("Received null or invalid number: {}", body);
                            }
                        } catch (InvalidNumberFormatException e) {
                            log.warn("Invalid number format exception: {}", e.getMessage());
                            exchange.setException(e);
                        }
                    });
        }

        from("timer://reporter?period=60000")
                .routeId("sum-reporter")
                .process(exchange -> {
                    long sum = accumulatorService.getAndReset();
                    String msg = "Sum of last 60s numbers = " + sum;
                    exchange.getMessage().setBody(msg);
                    log.info(msg);
                });
    }

}
