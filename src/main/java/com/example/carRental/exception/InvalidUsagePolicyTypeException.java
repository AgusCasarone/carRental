package com.example.carRental.exception;

public class InvalidUsagePolicyTypeException extends RuntimeException {
    public InvalidUsagePolicyTypeException(String message) {
        super(message);
    }
}