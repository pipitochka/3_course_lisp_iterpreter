package com.example.minilisp.tokens;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NilToken extends Token {
    @Override
    public String toString() {
        return "NIL";
    }
}
