package com.example.minilisp.tokens;

import lombok.Getter;

@Getter
public class StringToken extends Token {

    private final String value;

    protected StringToken(TokenType type, String value) {
        super(type);
        this.value = value;
    }
}
