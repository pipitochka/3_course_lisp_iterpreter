package com.example.minilisp.parser;

import com.example.minilisp.tokens.Token;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AtomExpression implements Expression {

    private final Token token;
}
