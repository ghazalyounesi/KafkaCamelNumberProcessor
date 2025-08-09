package com.example.kafka_camel_example.producer;

import com.example.kafka_camel_example.exception.InvalidNumberRangeException;
import lombok.Builder;
import lombok.ToString;

import java.util.concurrent.ThreadLocalRandom;

@ToString
public class RandomNumberProducer {

    private final int min;
    private final int max;

    @Builder
    public RandomNumberProducer(int min, int max) throws InvalidNumberRangeException {
        if (min > max) {
            throw new InvalidNumberRangeException("min must be less than or equal to max");
        }
        this.min = min;
        this.max = max;
    }

    public int produce() {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
