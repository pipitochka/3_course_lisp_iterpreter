package com.example.minilisp.parser;

import com.example.minilisp.exceptions.ParserException;
import com.example.minilisp.tokens.*;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class Parser {

    private final List<Token> tokens;
    private int pos = 0;

    public List<Expression> parseAll() {
        List<Expression> expressions = new ArrayList<>();
        while (pos < tokens.size()) {
            expressions.add(parseExp());
        }
        return expressions;
    }

    public Expression parse() {
        if (tokens.isEmpty()) {
            return new ListExpressions(Collections.emptyList());
        }
        return parseExp();
    }

    private Expression parseExp() {
        Token token = tokens.get(pos++);

        if (token instanceof QuoteToken) {
            Expression expr = parseExp();
            return new ListExpressions(
                    Arrays.asList(
                            new AtomExpression(new SpecialFormToken(SpecialForm.QUOTE)),
                            expr
                    )
            );
        }

        if (token instanceof LParenToken){
            List<Expression> list = new ArrayList<>();
            while (!(tokens.get(pos) instanceof RParenToken)){
                list.add(parseExp());
                if (pos >= tokens.size()) {
                    throw new ParserException("Incorrect paren count");
                }
            }
            pos++;
            return new ListExpressions(list);
        }

        return new AtomExpression(token);
    }

    public boolean isValid() {
        return pos >= tokens.size();
    }

}
