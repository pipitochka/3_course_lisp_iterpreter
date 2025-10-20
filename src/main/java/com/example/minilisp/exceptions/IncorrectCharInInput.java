package com.example.minilisp.exceptions;

public class IncorrectCharInInput extends RuntimeException {
    public IncorrectCharInInput(char message) {
        super("Incorrect char in input:" + message);
    }
}
