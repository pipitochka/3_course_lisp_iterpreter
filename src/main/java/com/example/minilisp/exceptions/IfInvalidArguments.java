package com.example.minilisp.exceptions;

public class IfInvalidArguments extends RuntimeException {
    public IfInvalidArguments(String message) {
        super("IfInvalidArguments" +  message);
    }
}
