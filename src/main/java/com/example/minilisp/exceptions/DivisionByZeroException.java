package com.example.minilisp.exceptions;

public class DivisionByZeroException extends RuntimeException {
    public DivisionByZeroException() {
        super("Divizion by zero");
    }
}
