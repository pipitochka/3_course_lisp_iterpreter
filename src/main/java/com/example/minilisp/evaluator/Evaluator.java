package com.example.minilisp.evaluator;

import com.example.minilisp.exceptions.DivisionByZeroException;
import com.example.minilisp.exceptions.InvalidAmountOfArgs;
import com.example.minilisp.exceptions.InvalidArgumentException;
import com.example.minilisp.exceptions.InvalidVariable;
import com.example.minilisp.parser.AtomExpression;
import com.example.minilisp.parser.Expression;
import com.example.minilisp.parser.ListExpressions;
import com.example.minilisp.tokens.*;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
public class Evaluator {

    private final HashMap<String, Token> enviroment;

    public Expression evaluate(Expression expression) {
        if (expression instanceof AtomExpression atom){
            return evalAtom(atom);
        } else if (expression instanceof ListExpressions list){
            return evalList(list);
        } else {
            throw new RuntimeException("Unsupported expression: " + expression);
        }
    }

    public Expression evalAtom(AtomExpression atom) {
        Token token = atom.getToken();

        if (token instanceof VariableToken variable){
            Token value = enviroment.get(variable.getValue());
            if (value == null) {
                throw new InvalidVariable(variable.getValue());
            }
        }

        return atom;
    }

    public Expression evalList(ListExpressions list) {
        if (list.getExpressions().isEmpty()){
            return list;
        }

        Expression first = list.getExpressions().get(0);
        Expression operator = evaluate(first);

        List<Expression> args = new ArrayList<>();
        for (int i = 1; i < list.getExpressions().size(); i++) {
            args.add(evaluate(list.getExpressions().get(i)));
        }

        if (operator instanceof AtomExpression atom){
            if (atom.getToken() instanceof OperatorToken operatorToken){
                return applyOperator(operatorToken, args);
            } else if (atom.getToken() instanceof SpecialFormToken specialFormToken){
                return applySpecialForm(specialFormToken, args);
            }
        }

        throw new RuntimeException("Unsupported operator: " + operator);
    }

    private AtomExpression applyOperator(OperatorToken operatorToken, List<Expression> args) {
        if (args.size() < 2){
            throw new InvalidAmountOfArgs(operatorToken.toString());
        }

        Expression first = args.get(0);
        if (!(first instanceof AtomExpression a)) throw new RuntimeException("Expected atom");
        Token acc = a.getToken();

        for (int i = 1; i < args.size(); i++) {
            Expression nextExpr = args.get(i);
            if (!(nextExpr instanceof AtomExpression b)) throw new RuntimeException("Expected atom");
            Token next = b.getToken();

            acc = combine(acc, next, operatorToken);
        }

        return new AtomExpression(acc);
    }

    private Expression applySpecialForm(SpecialFormToken specialFormToken, List<Expression> args) {
        return null;
    }

    private Token combine(Token a, Token b, OperatorToken op){
        switch (op.getOperator()){
            case ADD, SUB, MULT, DIV, MOD  -> {
                if (a instanceof IntToken aInt && b instanceof IntToken bInt){
                    switch (op.getOperator()){
                        case ADD -> {
                            return new IntToken(aInt.getValue() + bInt.getValue());
                        }
                        case SUB -> {
                            return new IntToken(aInt.getValue() - bInt.getValue());
                        }
                        case MULT -> {
                            return new IntToken(aInt.getValue() * bInt.getValue());
                        }
                        case DIV -> {
                            if (bInt.getValue() == 0){
                                throw new DivisionByZeroException();
                            }
                            return new IntToken(aInt.getValue() / bInt.getValue());
                        }
                        case MOD -> {
                            return new IntToken(aInt.getValue() % bInt.getValue());
                        }
                    }
                }
                if ((a instanceof IntToken || a instanceof DoulbeToken)
                        && (b instanceof IntToken || b instanceof DoulbeToken)){
                    double da = a instanceof IntToken ai ? ai.getValue() : ((DoulbeToken) a).getValue();
                    double db = b instanceof IntToken bi ? bi.getValue() : ((DoulbeToken) b).getValue();

                    switch (op.getOperator()){
                        case ADD -> {
                            return new DoulbeToken(da + db);
                        }
                        case SUB -> {
                            return new DoulbeToken(da - db);
                        }
                        case MULT -> {
                            return new DoulbeToken(da * db);
                        }
                        case DIV -> {
                            return new DoulbeToken(da / db);
                        }

                    }
                }
                if (a instanceof StringToken aString && b instanceof StringToken bString){
                    if (op.getOperator() == Operators.ADD){
                        return new StringToken(aString.getValue() + bString.getValue());
                    }
                }
            }
            case LT, GT, EQ, LE, GE, NE -> {
                if ((a instanceof IntToken || a instanceof DoulbeToken)
                        && (b instanceof IntToken || b instanceof DoulbeToken)){
                    double da = a instanceof IntToken ai ? ai.getValue() : ((DoulbeToken)a).getValue();
                    double db = b instanceof IntToken bi ? bi.getValue() : ((DoulbeToken)b).getValue();

                    boolean res = switch (op.getOperator()){
                        case LT -> da < db;
                        case GT -> da > db;
                        case LE -> da <= db;
                        case GE -> da >= db;
                        case NE -> da != db;
                        case EQ -> da == db;
                        default -> throw new InvalidArgumentException(op.toString());
                    };

                    return new BooleanToken(res);
                }
                if (a instanceof StringToken aString && b instanceof StringToken bString){
                    boolean res = switch (op.getOperator()){
                        case NE -> !aString.equals(bString);
                        case EQ -> aString.equals(bString);
                        default -> throw new InvalidArgumentException(op.toString());
                    };

                    return new BooleanToken(res);
                }
                if (a instanceof BooleanToken aBool && b instanceof BooleanToken bBool){
                    boolean da = aBool.isValue();
                    boolean db = bBool.isValue();
                    boolean res = switch (op.getOperator()){
                        case NE -> da != db;
                        case EQ -> da == db;
                        default -> throw new InvalidArgumentException(op.toString());
                    };

                    return new BooleanToken(res);
                }
            }
            default -> throw new RuntimeException("Unsupported operator: " + op);
        }
        throw new UnsupportedOperationException(op.toString());
    }
}
