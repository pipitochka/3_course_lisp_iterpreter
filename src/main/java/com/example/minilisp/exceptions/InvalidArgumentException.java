package com.example.minilisp.exceptions;

public class InvalidArgumentException extends RuntimeException {
    public InvalidArgumentException(String message) {
        super(
                "Invalid arguments for operator" + message);
    }
}
