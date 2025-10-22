package com.example.minilisp.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MacroExpression implements Expression {
    private final ListExpressions parameters;

    private final Expression body;
}
