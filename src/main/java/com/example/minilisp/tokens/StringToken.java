package com.example.minilisp.tokens;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StringToken extends Token {

    private final String value;


    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}
