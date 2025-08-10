package com.example.kafka_camel_example.aggregator;

import org.apache.camel.Exchange;
import org.apache.camel.AggregationStrategy;

public class NumberAggregator implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Long oldSum = oldExchange == null ? 0L : oldExchange.getIn().getBody(Long.class);
        Long newNumber = newExchange.getIn().getBody(Long.class);
        Long sum = oldSum + (newNumber != null ? newNumber : 0L);

        if (oldExchange == null) {
            newExchange.getIn().setBody(sum);
            return newExchange;
        } else {
            oldExchange.getIn().setBody(sum);
            return oldExchange;
        }
    }
}
