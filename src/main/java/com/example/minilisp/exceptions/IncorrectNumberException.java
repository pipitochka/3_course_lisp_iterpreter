package com.example.minilisp.exceptions;

public class IncorrectNumberException extends RuntimeException {
    public IncorrectNumberException() {
        super(
                "Incorrect number to parse."
        );
    }
}
