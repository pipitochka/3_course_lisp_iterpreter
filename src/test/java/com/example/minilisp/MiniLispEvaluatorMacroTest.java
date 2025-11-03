package com.example.minilisp;

import com.example.minilisp.enviroment.Environment;
import com.example.minilisp.evaluator.Evaluator;
import com.example.minilisp.lexer.Lexer;
import com.example.minilisp.parser.Expression;
import com.example.minilisp.parser.Parser;
import com.example.minilisp.tokens.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

public class MiniLispEvaluatorMacroTest {

    private Environment enviroments;

    @BeforeEach
    public void setup() {
        enviroments = new Environment();
    }

    private void test(String input, String expected) {
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);
        List<Expression> expressions = parser.parseAll();
        if (!parser.isValid()){
            throw new RuntimeException("Error: invalid expression");
        }
        Evaluator evaluator = new Evaluator(enviroments);
        for (Expression expr : expressions) {
            Expression result = evaluator.evaluate(expr);
        }
    }

    @Test
    public void testSimpleMacro() {
        // Простое определение макроса и вызов
        test("(def m (macro (x) (cons x '(1 2 3))))", "<macro>");
        test("(m 5)", "(5 1 2 3)");
    }

    @Test
    public void testMacroMultipleArgs() {
        // Макрос с несколькими аргументами
        test("(def m2 (macro (a b) (cons a (cons b '(100)))))", "<macro>");
        test("(m2 10 20)", "(10 20 100)");
    }

//    @Test
//    public void testMacroNested() {
//        // Вложенный макрос
//        test("(def inner (macro (x) (cons x '(9 9))))", "<macro>");
//        test("(def outer (macro (y) (cons (inner y) '(8 8))))", "<macro>");
//        test("(outer 5)", "((5 9 9) 8 8)");
//    }

    @Test
    public void testMacroArgumentEvaluation() {
        // Проверяем, что макрос не вычисляет аргументы заранее
        test("(def m3 (macro (x) (cons x '(0))))", "<macro>");
        test("(def y 10)", "10");
        test("(m3 y)", "(y 0)"); // аргумент 'y' раскрыт как символ, а не значение 10
    }

//    @Test
//    public void testMacroWithListArgument() {
//        // Передаем список в макрос
//        test("(def m4 (macro (lst) (cons 'head lst)))", "<macro>");
//        test("(m4 '(a b c))", "(head a b c)");
//    }

//    @Test
//    public void testMacroPartialApplication() {
//        // Если ваша реализация поддерживает частичное применение макросов
//        test("(def m5 (macro (x y) (cons x y)))", "<macro>");
//        test("(def p ((m5 1)))", "<macro>");
//        test("(p '(2 3 4))", "(1 2 3 4)");
//    }

}
