package com.example.minilisp.tokens;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class OperatorToken extends Token {

    private final Operators operator;
}
