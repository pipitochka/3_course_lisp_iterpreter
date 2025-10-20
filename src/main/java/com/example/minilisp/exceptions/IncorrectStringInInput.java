package com.example.minilisp.exceptions;

public class IncorrectStringInInput extends RuntimeException {
    public IncorrectStringInInput() {
        super(
                "Invalid string in input"
        );
    }
}
