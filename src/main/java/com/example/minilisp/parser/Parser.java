package com.example.minilisp.parser;

import com.example.minilisp.exceptions.IncorrectParenCounter;
import com.example.minilisp.tokens.LParenToken;
import com.example.minilisp.tokens.RParenToken;
import com.example.minilisp.tokens.Token;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class Parser {

    private final List<Token> tokens;
    private int pos = 0;

    public Expression parse() {
        if (tokens.isEmpty()) {
            return new ListExpressions(Collections.emptyList());
        }
        return parseExp();
    }

    private Expression parseExp() {
        Token token = tokens.get(pos++);

        if (token instanceof LParenToken){
            List<Expression> list = new ArrayList<>();
            while (!(tokens.get(pos) instanceof RParenToken)){
                list.add(parseExp());
                if (pos >= tokens.size()) {
                    throw new IncorrectParenCounter();
                }
            }
            pos++;
            return new ListExpressions(list);
        }

        return new AtomExpression(token);
    }

}
