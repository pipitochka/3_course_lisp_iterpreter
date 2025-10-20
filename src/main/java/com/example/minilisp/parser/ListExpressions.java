package com.example.minilisp.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class ListExpressions implements Expression {

    private final List<Expression> expressions;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < expressions.size(); i++) {
            sb.append(expressions.get(i).toString());
            if (i < expressions.size() - 1) sb.append(" ");
        }
        sb.append(")");
        return sb.toString();
    }
}
