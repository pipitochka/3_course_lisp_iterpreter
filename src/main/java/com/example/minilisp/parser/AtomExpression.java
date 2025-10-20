package com.example.minilisp.parser;

import com.example.minilisp.tokens.Token;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
public class AtomExpression implements Expression {

    private final Token token;

    @Override
    public String toString() {
        return token.toString();
    }
}
