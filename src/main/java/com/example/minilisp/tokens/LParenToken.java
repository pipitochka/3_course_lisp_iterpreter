package com.example.minilisp.tokens;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LParenToken extends Token {

    @Override
    public String toString() {
        return "(";
    }
}
