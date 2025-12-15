package com.example.minilisp.exceptions;

public class LexerException extends RuntimeException {
    public LexerException(String message) {
        super("Parser exception: " + message);
    }
}
