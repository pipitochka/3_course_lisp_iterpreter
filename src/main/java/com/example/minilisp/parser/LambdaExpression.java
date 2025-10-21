package com.example.minilisp.parser;

import com.example.minilisp.enviroment.Environment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class LambdaExpression implements Expression {
    private final ListExpressions parameters;

    private final Expression body;

    private final Environment closureEnv;


}
