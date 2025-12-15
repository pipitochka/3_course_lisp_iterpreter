package com.example.minilisp.exceptions;

public class EvaluatorException extends RuntimeException {
    public EvaluatorException(String message) {
        super("Evaluator exception: " + message);
    }
}
