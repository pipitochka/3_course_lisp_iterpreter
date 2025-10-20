package com.example.minilisp.tokens;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BooleanToken extends Token {

    boolean value;

    @Override
    public String toString() {
        return value ? "true" : "false";
    }
}
