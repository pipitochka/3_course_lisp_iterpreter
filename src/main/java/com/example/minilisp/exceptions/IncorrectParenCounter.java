package com.example.minilisp.exceptions;

public class IncorrectParenCounter extends RuntimeException {

    public IncorrectParenCounter() {
        super("Not enough parens");
    }
}
