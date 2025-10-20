package com.example.minilisp.exceptions;

public class UnsupportedOperationException extends RuntimeException {
    public UnsupportedOperationException(String message) {
        super("UnsupportedOperationException: " + message);
    }
}
