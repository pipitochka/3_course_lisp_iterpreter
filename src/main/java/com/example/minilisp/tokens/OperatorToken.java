package com.example.minilisp.tokens;

import lombok.Getter;

@Getter
public class OperatorToken extends Token {

    private final Operators operator;

    public OperatorToken(TokenType type, Operators operator) {
        super(type);
        this.operator = operator;
    }
}
