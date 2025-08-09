package com.example.kafka_camel_example.exception;

public class InvalidNumberFormatException extends Exception {

    public InvalidNumberFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}