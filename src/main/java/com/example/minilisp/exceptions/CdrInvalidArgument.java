package com.example.minilisp.exceptions;

public class CdrInvalidArgument extends RuntimeException {

    public CdrInvalidArgument(String message) {
        super("CdrInvalidArgument: " + message);
    }
}
