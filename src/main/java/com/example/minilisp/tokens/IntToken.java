package com.example.minilisp.tokens;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IntToken extends Token {

    private final int value;
}
