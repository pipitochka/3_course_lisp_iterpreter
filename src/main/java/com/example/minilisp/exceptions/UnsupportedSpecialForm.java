package com.example.minilisp.exceptions;

public class UnsupportedSpecialForm extends RuntimeException {
    public UnsupportedSpecialForm(String message) {
        super(
                "Unsupported special form: " + message);
    }
}
