package com.example.minilisp.tokens;

import lombok.Getter;

@Getter
public class IntToken extends Token {

    private final int value;

    protected IntToken(TokenType type, int value) {
        super(type);
        this.value = value;
    }
}
