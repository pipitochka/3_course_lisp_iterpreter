package com.example.minilisp.tokens;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class QuoteToken extends Token {
    @Override
    public String toString() {
        return "'";
    }
}
