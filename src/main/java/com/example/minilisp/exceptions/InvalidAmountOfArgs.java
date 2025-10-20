package com.example.minilisp.exceptions;

public class InvalidAmountOfArgs extends RuntimeException {
    public InvalidAmountOfArgs(String message) {
        super("Not enought args for:" + message);
    }
}
