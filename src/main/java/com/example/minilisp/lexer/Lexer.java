package com.example.minilisp.lexer;

import com.example.minilisp.exceptions.IncorrectCharInInput;
import com.example.minilisp.exceptions.IncorrectNumberException;
import com.example.minilisp.exceptions.IncorrectParenCounter;
import com.example.minilisp.exceptions.IncorrectStringInInput;
import com.example.minilisp.tokens.*;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private final String input;
    private int pos = 0;
    private int parenCount = 0;

    public Lexer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (pos < input.length()) {
            char c = input.charAt(pos);

            switch (c) {
                case ' ', '\t', '\r', '\n':
                    pos++;
                    continue;
                case '(':
                    tokens.add(new LParenToken());
                    parenCount++;
                    pos++;
                    continue;
                case ')':
                    tokens.add(new RParenToken());
                    parenCount--;
                    if (parenCount < 0) {
                        throw new IncorrectParenCounter();
                    }
                    pos++;
                    continue;
                case '\'':
                    tokens.add(new QuoteToken());
                    pos++;
                    continue;
                case '"':
                    tokens.add(readString());
                    continue;
                default:
                    if (Character.isDigit(c) || (c == '-' && Character.isDigit(input.charAt(pos + 1)))) {
                        tokens.add(readNumber());
                        continue;
                    } else if (isSymbolStart(c)) {
                        tokens.add(readSymbol());
                        continue;
                    } else {
                        throw new IncorrectCharInInput(c);
                    }
            }

        }

        if (parenCount != 0) {
            throw new IncorrectParenCounter();
        }

        return tokens;
    }

    private Token readString(){
        pos++;

        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && input.charAt(pos) != '"'){
            char current = input.charAt(pos);
            sb.append(current);
            pos++;
        }
        if (input.charAt(pos) != '"'){
            throw new IncorrectStringInInput();
        }

        pos++;
        return new StringToken(sb.toString());
    }

    private Token readNumber(){
        int start = pos;

        if (input.charAt(pos) == '-') {
            pos++;
        }

        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            pos++;
        }

        if (pos < input.length() && input.charAt(pos) == '.') {
            pos++;
            while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
                pos++;
            }
        }

        String numberStr = input.substring(start, pos);
        try {
            if (numberStr.contains(".")) {
                return new DoulbeToken(Double.parseDouble(numberStr));
            } else {
                return new IntToken(Integer.parseInt(numberStr));
            }
        } catch (NumberFormatException e) {
            throw new IncorrectNumberException();
        }
    }


    private Token readSymbol() {
        int start = pos;

        while (pos < input.length() && isSymbolChar(input.charAt(pos))) {
            pos++;
        }

        String symbol = input.substring(start, pos);
        return classifySymbol(symbol);
    }

    private Token classifySymbol(String symbol) {
        switch (symbol) {
            case "true", "false":
                return new BooleanToken(Boolean.parseBoolean(symbol));
            case "nil":
                return new NilToken();
            case "quote": return new SpecialFormToken(SpecialForm.QUOTE);
            case "eval": return new SpecialFormToken(SpecialForm.EVAL);
            case "typeof": return new SpecialFormToken(SpecialForm.TYPEOF);
            case "cons": return new SpecialFormToken(SpecialForm.CONS);
            case "car": return new SpecialFormToken(SpecialForm.CAR);
            case "cdr": return new SpecialFormToken(SpecialForm.CDR);
            case "if": return new SpecialFormToken(SpecialForm.IF);
            case "do": return new SpecialFormToken(SpecialForm.DO);
            case "print": return new SpecialFormToken(SpecialForm.PRINT);
            case "read": return new SpecialFormToken(SpecialForm.READ);
            case "symbol": return new SpecialFormToken(SpecialForm.SYMBOL);
            case "def": return new SpecialFormToken(SpecialForm.DEF);
            case "set": return new SpecialFormToken(SpecialForm.SET);
            case "+": return new OperatorToken(Operators.ADD);
            case "-": return new OperatorToken(Operators.SUB);
            case "*": return new OperatorToken(Operators.MULT);
            case "%": return new OperatorToken(Operators.MOD);
            case "/": return new OperatorToken(Operators.DIV);
            case "=": return new OperatorToken(Operators.EQ);
            case ">": return new OperatorToken(Operators.GT);
            case "<": return new OperatorToken(Operators.LT);
            case ">=": return new OperatorToken(Operators.GE);
            case "<=": return new OperatorToken(Operators.LE);
            case "!=": return new OperatorToken(Operators.NE);
            case "==": return new OperatorToken(Operators.EQ);
            default:
                return new VariableToken(symbol);
        }
    }


    private boolean isSymbolStart(char c) {
        return Character.isLetter(c) || "!$%&*+-./:<=>?@^_~".indexOf(c) >= 0;
    }

    private boolean isSymbolChar(char c) {
        return isSymbolStart(c) || Character.isDigit(c);
    }
}
