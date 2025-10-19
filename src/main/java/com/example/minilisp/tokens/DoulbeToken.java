package com.example.minilisp.tokens;

import lombok.Getter;

@Getter
public class DoulbeToken extends Token {

    private final double value;

    protected DoulbeToken(TokenType type, double value) {
        super(type);
        this.value = value;
    }
}
