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

public class MiniLispEvaluatorLambdaAndDamdaTest {

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
    public void testLambda() {
        test("((lambda (x y) (+ x y)) 2 3)", "6");
    }

    @Test
    public void testLambda2() {
        test("(def add (lambda (a b) (+ a b)))\n"  +
                "(add 10 20)", "30");
    }

    @Test
    public void testLambda3() {
        test("(def fact (lambda (n)\n" +
                "    (if (< n 2)\n" +
                "        1\n" +
                "        (* n (fact (- n 1)))\n" +
                "    )\n" +
                "))"  +
                "(fact 5)", "120");
    }

    @Test
    public void testLambdaPartialApplication() {
        // Частичное применение: (lambda (x y) (+ x y)) 1
        // → возвращает новую лямбду, которая ждет y
        test("(def add2 ((lambda (x y) (+ x y)) 1))", "<lambda>");
        // Теперь вызываем результат с недостающим аргументом
        test("(add2 5)", "6");
    }

    @Test
    public void testLambdaArgGathering() {
        // Лишние аргументы загребаются в последний параметр:
        // (lambda (x y) ...) и вызов с 4 аргументами → y получает список (2 3 4)
        test("(def f (lambda (x y) (cons x y)))", "<lambda>");
        test("(f 1 2 3 4)", "(1 2 3 4)");
    }

    @Test
    public void testDambdaPartialApplication() {
        test("(def f ((dambda (a b c) (cons a (cons b (cons c '())))) 1 2))", "<dambda>");
        // Остался один параметр
        test("(f 3)", "(1 2 3)");
    }

    @Test
    public void testDambdaArgGathering() {
        // Проверяем "загребание" при dambda
        test("(def g (dambda (a rest) (cons a rest)))", "<dambda>");
        test("(g 1 2 3 4 5)", "(1 2 3 4 5)");
    }

    @Test
    public void testLambdaPartialApplicationMultiple() {
        // Частичное применение с lambda, потом полный вызов
        test("(def add3 ((lambda (x y z) (+ x (+ y z))) 2))", "<lambda>");
        test("(add3 3 4)", "9"); // 2 + 3 + 4 = 9
    }

    @Test
    public void testLambdaPartialApplicationExtraArgs() {
        // Lambda с загребанием лишних аргументов
        test("(def f ((lambda (x y) (cons x y)) 1))", "<lambda>");
        test("(f 2 3 4)", "(1 2 3 4)"); // y получает список (2 3 4)
    }

    @Test
    public void testDambdaPartialApplicationMultiple() {
        // Частичное применение dambda
        test("(def f ((dambda (a b c d) (cons a (cons b (cons c (cons d '()))))) 1 2))", "<dambda>");
        test("(f 3 4)", "(1 2 3 4)");
    }

    @Test
    public void testDambdaPartialApplicationExtraArgs() {
        // dambda загребает лишние аргументы в последний параметр
        test("(def f (dambda (x y) (cons x y)))", "<dambda>");
        test("(f 10 20 30 40)", "(10 20 30 40)"); // y получает список (20 30 40)
    }

    @Test
    public void testLambdaChainedPartialApplication() {
        // Несколько частичных применений lambda подряд
        test("(def add4 ((lambda (a b c d) (+ a (+ b (+ c d)))) 1 2))", "<lambda>");
        test("(def add2 ((add4 3)))", "<lambda>");
        test("(add2 4)", "10"); // 1+2+3+4 = 10
    }

    @Test
    public void testDambdaChainedPartialApplication() {
        // Несколько частичных применений dambda подряд
        test("(def f ((dambda (a b c) (cons a (cons b (cons c '())))) 5))", "<dambda>");
        test("(def g ((f 6)))", "<dambda>");
        test("(g 7)", "(5 6 7)");
    }
}
