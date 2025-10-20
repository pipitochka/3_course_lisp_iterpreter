package com.example.minilisp.tokens;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public class DoulbeToken extends Token {

    private final double value;

    @Override
    public String toString() {
        return Double.toString(value);
    }
}
