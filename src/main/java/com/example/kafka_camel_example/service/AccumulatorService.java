package com.example.kafka_camel_example.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class AccumulatorService {

    private final AtomicLong sum = new AtomicLong(0);

    public void add(long value) {
        sum.addAndGet(value);
    }

    public long getAndReset() {
        return sum.getAndSet(0);
    }
}
