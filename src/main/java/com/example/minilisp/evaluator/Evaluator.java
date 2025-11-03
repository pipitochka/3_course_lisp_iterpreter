package com.example.minilisp.evaluator;

import com.example.minilisp.enviroment.Environment;
import com.example.minilisp.exceptions.*;
import com.example.minilisp.parser.*;
import com.example.minilisp.tokens.*;

import java.lang.UnsupportedOperationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


public class Evaluator {

    private final Environment environment;

    private Scanner inputScanner;

    public Evaluator(Environment environment) {

        this.environment = environment;
    }

    public Evaluator(Environment environment, Scanner scanner) {
        this.environment = environment;
        inputScanner = scanner;
    }

    public Expression evaluate(Expression expression) {
        if (expression instanceof AtomExpression atom){
            return evalAtom(atom);
        } else if (expression instanceof ListExpressions list){
            return evalList(list);
        } else if (expression instanceof LambdaExpression lambda){
            return lambda;
        }

        else {
            throw new RuntimeException("Unsupported expression: " + expression);
        }
    }

    public Expression evalAtom(AtomExpression atom) {
        Token token = atom.getToken();

        if (token instanceof VariableToken variable){
            Expression value = environment.get(variable.getValue());
            if (value == null) {
                throw new InvalidVariable(variable.getValue());
            }
            return value;
        }

        return atom;
    }

    public Expression evalList(ListExpressions list) {
        if (list.getExpressions().isEmpty()){
            return list;
        }

        Expression first = list.getExpressions().get(0);

        if (first instanceof AtomExpression atom) {
            if (atom.getToken() instanceof OperatorToken operatorToken) {
                List<Expression> args = new ArrayList<>();
                for (int i = 1; i < list.getExpressions().size(); i++) {
                    args.add(evaluate(list.getExpressions().get(i)));
                }
                return applyOperator(operatorToken, args);
            } else if (atom.getToken() instanceof SpecialFormToken specialFormToken) {
                List<Expression> args = list.getExpressions().subList(1, list.getExpressions().size());
                return applySpecialForm(specialFormToken, args);
            }
        }
        first = evaluate(first);
        if (first instanceof LambdaExpression lambdaExpression) {
            ListExpressions argsList = new ListExpressions(list.getExpressions().subList(1, list.getExpressions().size()));
            return applyLambda(lambdaExpression, argsList);
        }
        if (first instanceof DambdaExpression dambdaExpression) {
            ListExpressions argsList = new ListExpressions(list.getExpressions().subList(1, list.getExpressions().size()));
            return applyDambda(dambdaExpression, argsList);
        }
        if (first instanceof MacroExpression macroExpression) {
            ListExpressions argsList = new ListExpressions(list.getExpressions().subList(1, list.getExpressions().size()));
            return applyMacro(macroExpression, argsList);
        }

        throw new InvalidArgumentException("Not callable" + first);
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

    private Expression applySpecialForm(SpecialFormToken specialFormToken, List<Expression> args) {
        switch (specialFormToken.getSpecialForm()){
            case QUOTE -> {
                if (args.size() != 1) {
                    throw new QuoteTooManyArgs(args.size());
                }
                return args.get(0);
            }
            case EVAL -> {
                if (args.size() != 1) {
                    throw new InvalidArgumentException("EVAL expects 1 argument");
                }
                Expression value = evaluate(args.get(0));
                if (value instanceof ListExpressions list) {
                    return evaluate(list);
                } else {
                    throw new InvalidArgumentException("EVAL expects a list, got: " + value);
                }
            }
            case TYPEOF -> {
                return new ListExpressions(
                        args.stream()
                                .map(this::evaluate)
                                .map(expr -> {
                                    if (expr instanceof AtomExpression atom) {
                                        if (atom.getToken() instanceof StringToken) {
                                            return new AtomExpression(new StringToken("string"));
                                        }
                                        if (atom.getToken() instanceof IntToken) {
                                            return new AtomExpression(new StringToken("int"));
                                        }
                                        if (atom.getToken() instanceof DoulbeToken) {
                                            return new AtomExpression(new StringToken("double"));
                                        }
                                        if (atom.getToken() instanceof NilToken) {
                                            return new AtomExpression(new StringToken("nil"));
                                        }
                                        if (atom.getToken() instanceof BooleanToken) {
                                            return new AtomExpression(new StringToken("boolean"));
                                        }
                                        if (atom.getToken() instanceof SpecialFormToken) {
                                            return new AtomExpression(new StringToken("SF"));
                                        }
                                        if (atom.getToken() instanceof OperatorToken) {
                                            return new AtomExpression(new StringToken("operator"));
                                        }
                                    }
                                    if (expr instanceof ListExpressions list) {
                                        return new AtomExpression(new StringToken("list"));
                                    }
                                    return new AtomExpression(new StringToken("unknown"));
                                })
                                .<Expression>map(e->e)
                                .toList());
            }
            case CONS -> {
                if (args.size() != 2) {
                    throw new InvalidArgumentException("CONS expects 2 arguments");
                }
                Expression first = evaluate(args.get(0));
                Expression second = evaluate(args.get(1));

                List<Expression> result = new ArrayList<>();
                result.add(first);

                if (second instanceof ListExpressions list) {
                    result.addAll(list.getExpressions());
                } else {
                    result.add(second);
                }

                return new ListExpressions(result);
            }
            case CAR -> {
                var first = args.get(0);
                var calculed = evaluate(first);
                if (calculed instanceof AtomExpression atom) {
                    return atom;
                } else if (calculed instanceof ListExpressions list) {
                    if (list.getExpressions().isEmpty()) {
                        return new AtomExpression(new NilToken());
                    }
                    return list.getExpressions().get(0);
                }
            }
            case CDR -> {
                var first = evaluate(args.get(0));
                if (first instanceof ListExpressions list) {
                    return new ListExpressions(
                            list.getExpressions().subList(1, list.getExpressions().size())
                    );
                }
                throw new CdrInvalidArgument(args.toString());
            }
            case IF -> {
                if (args.size() != 3) {
                    throw new IfInvalidArguments(args.toString());
                }
                var calculed = evaluate(args.get(0));
                if (calculed instanceof AtomExpression atom) {
                    if (atom.getToken() instanceof BooleanToken booleanToken) {
                        if (booleanToken.isValue()) {
                            return evaluate(args.get(1));
                        }
                        else {
                            return evaluate(args.get(2));
                        }
                    }
                }
                throw new IfInvalidArguments(args.toString());
            }
            case DO -> {
                Expression expr = new AtomExpression(new NilToken());
                for (Expression expression : args) {
                    expr = evaluate(expression);
                }
                return expr;
            }
            case PRINT -> {
                if (args.size() != 1) {
                    throw new PrintInvalidArgumentException(args.toString());
                }
                var result = evaluate(args.get(0));
                System.out.println(result);
                return result;
            }
            case READ -> {
                String input = inputScanner.nextLine().trim();

                if (input.equals("true") || input.equals("false")) {
                    return new AtomExpression(new BooleanToken(Boolean.parseBoolean(input)));
                }

                try {
                    if (input.contains(".")) {
                        double d = Double.parseDouble(input);
                        return new AtomExpression(new DoulbeToken(d));
                    } else {
                        int i = Integer.parseInt(input);
                        return new AtomExpression(new IntToken(i));
                    }
                } catch (NumberFormatException e) {
                    return new AtomExpression(new StringToken(input));
                }
            }
            case SYMBOL -> {
                if (args.size() != 1) {
                    throw new SymbolArgumentException(args.toString());
                }
                var result = evaluate(args.get(0));
                if (result instanceof AtomExpression atom) {
                    if (atom.getToken() instanceof StringToken stringToken) {
                        return new AtomExpression(new VariableToken(stringToken.getValue()));
                    }
                }
                throw new SymbolArgumentException(result.toString());
            }
            case DEF -> {
                if (args.size() != 2) {
                    throw new DefArgumentException(args.toString());
                }

                var keyExpr = args.get(0);
                var valueExpr = evaluate(args.get(1));

                if (keyExpr instanceof AtomExpression keyAtom) {
                    if (keyAtom.getToken() instanceof VariableToken variableToken) {
                        if (!environment.contains(variableToken.getValue())) {
                            environment.define(variableToken.getValue(), valueExpr);
                            return valueExpr;
                        }
                        else {
                            throw new DefArgumentException("Exsits");
                        }
                    } else {
                        throw new DefArgumentException("Expected a string as variable name");
                    }
                } else {
                    throw new DefArgumentException("Invalid arguments for DEF");
                }
            }
            case SET -> {
                if (args.size() != 2) {
                    throw new SetArgumentException(args.toString());
                }

                var keyExpr = args.get(0);
                var valueExpr = evaluate(args.get(1));

                if (!(keyExpr instanceof AtomExpression keyAtom)) {
                    throw new SetArgumentException("Expected variable name as first argument");
                }

                if (!(keyAtom.getToken() instanceof VariableToken variableToken)) {
                    throw new SetArgumentException("Expected string as variable name");
                }

                String varName = variableToken.getValue();

                if (!environment.contains(varName)) {
                    throw new InvalidVariable(varName);
                }

                environment.set(varName, valueExpr);
                return valueExpr;
            }
            case LAMBDA -> {
                if (args.size() != 2) {
                    throw new SymbolArgumentException(args.toString());
                }

                Expression paramsExpr = args.get(0);
                Expression bodyExpr = args.get(1);

                if (paramsExpr instanceof ListExpressions list) {
                    return new LambdaExpression(list, bodyExpr, environment);
                }

            }
            case DAMBDA -> {
                if (args.size() != 2) {
                    throw new SymbolArgumentException(args.toString());
                }

                Expression paramsExpr = args.get(0);
                Expression bodyExpr = args.get(1);

                if (paramsExpr instanceof ListExpressions list) {
                    return new DambdaExpression(list, bodyExpr);
                }
            }
            case MACRO -> {
                if (args.size() != 2) {
                    throw new SymbolArgumentException(args.toString());
                }

                Expression paramsExpr = args.get(0);
                Expression bodyExpr = args.get(1);

                if (paramsExpr instanceof ListExpressions list) {
                    return new MacroExpression(list, bodyExpr);
                }

            }
            default -> throw new UnsupportedSpecialForm(specialFormToken.toString());
        }
        throw new UnsupportedSpecialForm(specialFormToken.toString());
    }

    private Expression applyLambda(LambdaExpression lambda, ListExpressions listExpressions) {
        List<Expression> args = listExpressions.getExpressions();
        List<Expression> params = lambda.getParameters().getExpressions();

        Environment localEnv = new Environment(lambda.getClosureEnv());

        int paramCount = params.size();
        int argCount = args.size();

//        for (int i = 0; i < params.size(); i++) {
//            Expression paramExpr = params.get(i);
//
//            if (!(paramExpr instanceof AtomExpression atom) || !(atom.getToken() instanceof VariableToken varToken)) {
//                throw new RuntimeException("Invalid parameter name in lambda");
//            }
//
//            localEnv.define(varToken.getValue(), evaluate(args.get(i)));
//        }

        if (argCount > paramCount) {
            for (int i = 0; i < paramCount; i++) {
                Expression paramExpr = params.get(i);
                if (!(paramExpr instanceof AtomExpression atom) || !(atom.getToken() instanceof VariableToken varToken)) {
                    throw new RuntimeException("Invalid parameter name in lambda");
                }

                if (i == paramCount - 1) {
                    List<Expression> lastArgs = new ArrayList<>();
                    for (int j = paramCount - 1; j < argCount; j++) {
                        lastArgs.add((args.get(j)));
                    }
                    ListExpressions newListExpressions = new ListExpressions(lastArgs);
                    localEnv.define(varToken.getValue(), newListExpressions);
                } else {
                    localEnv.define(varToken.getValue(), args.get(i));
                }
            }
        } else if (argCount < paramCount) {
            List<Expression> remainingParams = params.subList(argCount, paramCount);

            Environment partiallyAppliedEnv = new Environment(lambda.getClosureEnv());
            for (int i = 0; i < argCount; i++) {
                Expression paramExpr = params.get(i);
                if (!(paramExpr instanceof AtomExpression atom) || !(atom.getToken() instanceof VariableToken varToken)) {
                    throw new RuntimeException("Invalid parameter name in lambda");
                }
                partiallyAppliedEnv.define(varToken.getValue(), args.get(i));
            }

            return new LambdaExpression(
                    new ListExpressions(remainingParams),
                    lambda.getBody(),
                    partiallyAppliedEnv
            );
        } else {
            for (int i = 0; i < paramCount; i++) {
                Expression paramExpr = params.get(i);
                if (!(paramExpr instanceof AtomExpression atom) || !(atom.getToken() instanceof VariableToken varToken)) {
                    throw new RuntimeException("Invalid parameter name in lambda");
                }
                localEnv.define(varToken.getValue(), evaluate(args.get(i)));
            }
        }

        Evaluator localEvaluator = new Evaluator(localEnv, inputScanner);
        return localEvaluator.evaluate(lambda.getBody());
    }

    private Expression applyDambda(DambdaExpression dambda, ListExpressions listExpressions) {
        List<Expression> args = listExpressions.getExpressions();
        List<Expression> params = dambda.getParameters().getExpressions();

        Environment localEnv = new Environment(environment);

        int paramCount = params.size();
        int argCount = args.size();

        if (argCount > paramCount) {
            // Загребаем лишние аргументы в последний параметр
            for (int i = 0; i < paramCount; i++) {
                Expression paramExpr = params.get(i);
                if (!(paramExpr instanceof AtomExpression atom) || !(atom.getToken() instanceof VariableToken varToken)) {
                    throw new RuntimeException("Invalid parameter name in dambda");
                }

                if (i == paramCount - 1) {
                    List<Expression> restArgs = new ArrayList<>();
                    for (int j = i; j < argCount; j++) {
                        restArgs.add((args.get(j)));
                    }
                    localEnv.define(varToken.getValue(), new ListExpressions(restArgs));
                } else {
                    localEnv.define(varToken.getValue(), (args.get(i)));
                }
            }
        } else if (argCount < paramCount) {
            // Возвращаем новую даммбду с оставшимися параметрами
            List<Expression> remainingParams = params.subList(argCount, paramCount);

            Environment partiallyAppliedEnv = new Environment(environment);
            for (int i = 0; i < argCount; i++) {
                Expression paramExpr = params.get(i);
                if (!(paramExpr instanceof AtomExpression atom) || !(atom.getToken() instanceof VariableToken varToken)) {
                    throw new RuntimeException("Invalid parameter name in dambda");
                }
                partiallyAppliedEnv.define(varToken.getValue(), args.get(i));
            }

            return new LambdaExpression(
                    new ListExpressions(new ArrayList<>(remainingParams)),
                    dambda.getBody(),
                    partiallyAppliedEnv
            );
        } else {
            // Равное количество
            for (int i = 0; i < paramCount; i++) {
                Expression paramExpr = params.get(i);
                if (!(paramExpr instanceof AtomExpression atom) || !(atom.getToken() instanceof VariableToken varToken)) {
                    throw new RuntimeException("Invalid parameter name in dambda");
                }
                localEnv.define(varToken.getValue(), args.get(i));
            }
        }

        Evaluator localEvaluator = new Evaluator(localEnv, inputScanner);
        return localEvaluator.evaluate(dambda.getBody());
    }


    private Expression applyMacro(MacroExpression macro, ListExpressions listExpressions) {
        List<Expression> args = listExpressions.getExpressions();
        List<Expression> params = macro.getParameters().getExpressions();

        if (args.size() != params.size()) {
            throw new RuntimeException("Wrong number of arguments for lambda");
        }

        Environment localEnv = new Environment();

        for (int i = 0; i < params.size(); i++) {
            Expression paramExpr = params.get(i);

            if (!(paramExpr instanceof AtomExpression atom) || !(atom.getToken() instanceof VariableToken varToken)) {
                throw new RuntimeException("Invalid parameter name in dambda");
            }

            localEnv.define(varToken.getValue(), args.get(i));
        }


        var expr = macroExpand(macro.getBody(), localEnv);

        return evaluate(expr);
    }

    private Expression macroExpand(Expression body, Environment env){

        if (body instanceof AtomExpression atom) {
            if (atom.getToken() instanceof VariableToken varToken) {
                try {
                    return env.get(varToken.getValue());
                } catch (InvalidVariable e) {
                    return body;
                }
            }
        }
        if (body instanceof ListExpressions listExpressions) {
            return new ListExpressions(listExpressions.getExpressions()
                    .stream()
                    .map(el -> macroExpand(el, env))
                    .toList()
            );
        }
        return body;
    }

}

