package com.example.minilisp.exceptions;

public class PrintInvalidArgumentException extends RuntimeException {
    public PrintInvalidArgumentException(String message) {
        super("PrintInvalidArgumentException" + message);
    }
}
