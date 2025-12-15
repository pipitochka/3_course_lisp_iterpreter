package com.example.minilisp.exceptions;

public class ParserException extends RuntimeException {
    public ParserException(String message) {
        super("Parser exception: " + message);
    }
}
