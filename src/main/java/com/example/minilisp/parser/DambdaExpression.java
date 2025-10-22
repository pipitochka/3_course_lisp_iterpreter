package com.example.minilisp.parser;

import com.example.minilisp.enviroment.Environment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class DambdaExpression implements Expression {
    private final ListExpressions parameters;

    private final Expression body;
}
