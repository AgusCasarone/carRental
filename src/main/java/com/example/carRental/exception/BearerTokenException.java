package com.example.carRental.exception;

public class BearerTokenException extends RuntimeException {

    public BearerTokenException(String message) {
        super(message);
    }

}