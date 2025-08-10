package com.example.kafka_camel_example.routes;

import com.example.kafka_camel_example.aggregator.NumberAggregator;
import com.example.kafka_camel_example.config.KafkaProperties;
import com.example.kafka_camel_example.exception.InvalidNumberFormatException;
import com.example.kafka_camel_example.service.AccumulatorService;
import com.example.kafka_camel_example.util.NumberExtractor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConsumerRoute extends RouteBuilder {

    private final KafkaProperties kafkaProperties;
    private final AccumulatorService accumulatorService;
    private final NumberAggregator numberAggregator = new NumberAggregator();

    @Value("${app.useKafka:true}")
    private boolean useKafka;

    public ConsumerRoute(@NonNull KafkaProperties kafkaProperties, @NonNull AccumulatorService accumulatorService) {
        this.kafkaProperties = kafkaProperties;
        this.accumulatorService = accumulatorService;
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
                                exchange.getMessage().setBody(number);
                            } else {
                                log.warn("Received null or invalid number: {}", body);
                                exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
                            }
                        } catch (InvalidNumberFormatException e) {
                            log.warn("Invalid number format exception: {}", e.getMessage());
                            exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
                        }
                    })
                    .aggregate(constant(true), numberAggregator)
                    .completionInterval(60000)
                    .process(exchange -> {
                        Long sum = exchange.getIn().getBody(Long.class);
                        if (sum != null) {
                            accumulatorService.add(sum);
                            String msg = "Sum of last 60s numbers (aggregated) = " + sum;
                            exchange.getMessage().setBody(msg);
                            log.info(msg);
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
                                exchange.getMessage().setBody(number);
                            } else {
                                log.warn("Received null or invalid number: {}", body);
                                exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
                            }
                        } catch (InvalidNumberFormatException e) {
                            log.warn("Invalid number format exception: {}", e.getMessage());
                            exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
                        }
                    })
                    .aggregate(constant(true), numberAggregator)
                    .completionInterval(60000)
                    .process(exchange -> {
                        Long sum = exchange.getIn().getBody(Long.class);
                        if (sum != null) {
                            accumulatorService.add(sum);
                            String msg = "Sum of last 60s numbers (aggregated) = " + sum;
                            exchange.getMessage().setBody(msg);
                            log.info(msg);
                        }
                    });
        }
    }
}
