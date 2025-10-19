package com.example.minilisp.tokens;

import lombok.Getter;

@Getter
public class BooleanToken extends Token {

    boolean value;

    protected BooleanToken(TokenType type, boolean value) {
        super(type);
        this.value = value;
    }
}
