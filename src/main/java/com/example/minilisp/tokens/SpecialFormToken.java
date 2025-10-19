package com.example.minilisp.tokens;

import lombok.Getter;

@Getter
public class SpecialFormToken extends Token {

    private final SpecialForm specialForm;

    protected SpecialFormToken(TokenType type, SpecialForm specialForm) {
        super(type);
        this.specialForm = specialForm;
    }
}
