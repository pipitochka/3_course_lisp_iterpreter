package com.example.minilisp.tokens;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RParenToken extends Token {
    @Override
    public String toString() {
        return ")";
    }
}
