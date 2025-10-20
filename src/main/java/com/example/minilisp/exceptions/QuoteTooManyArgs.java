package com.example.minilisp.exceptions;

public class QuoteTooManyArgs extends RuntimeException {
    public QuoteTooManyArgs(int count) {
        super("Too many args for quote :" + count);
    }
}
