package com.example.minilisp.tokens;

import lombok.Getter;

@Getter
public abstract class Token {

    private final TokenType type;

    protected Token(TokenType type) {
        this.type = type;
    }

}
