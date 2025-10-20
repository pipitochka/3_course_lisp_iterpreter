package com.example.minilisp.tokens;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OperatorToken extends Token {

    private final Operators operator;
}
