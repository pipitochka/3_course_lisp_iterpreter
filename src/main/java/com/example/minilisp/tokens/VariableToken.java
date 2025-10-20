package com.example.minilisp.tokens;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VariableToken extends Token {

    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
