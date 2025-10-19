package com.example.minilisp.tokens;

import lombok.Getter;

@Getter
public class VariableToken extends Token {

    private final String value;

    protected VariableToken(TokenType type, String value) {
        super(type);
        this.value = value;
    }
}
