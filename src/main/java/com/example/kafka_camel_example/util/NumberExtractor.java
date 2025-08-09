package com.example.kafka_camel_example.util;

import com.example.kafka_camel_example.exception.InvalidNumberFormatException;

import java.util.Optional;

public final class NumberExtractor {

    private NumberExtractor() {}

    public static Long extractNumber(Object body) throws InvalidNumberFormatException {
        if (body instanceof Number) {
            return ((Number) body).longValue();
        }
        if (body instanceof String) {
            try {
                return Long.parseLong((String) body);
            } catch (NumberFormatException e) {
                throw new InvalidNumberFormatException("Invalid number format in String: " + body, e);
            }
        }
        if (body instanceof byte[]) {
            try {
                return Long.parseLong(new String((byte[]) body));
            } catch (NumberFormatException e) {
                throw new InvalidNumberFormatException("Invalid number format in byte[]", e);
            }
        }
        return null;
    }

}
