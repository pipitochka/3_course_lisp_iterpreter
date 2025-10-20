package com.example.minilisp.parser;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ListExpressions implements Expression {

    private final List<Expression> expressions;
}
