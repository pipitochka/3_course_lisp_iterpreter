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

public class MiniLispEvaluatorTest {

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
    public void testQuoteAndApostrophe() {
        test("'(1 2 3)", "(1 2 3)");
        test("(quote (a b c))", "(a b c)");
    }

    @Test
    public void testCar() {
        test("(car '(1 2 3))", "1");
        test("(car '((a b) c d))", "(a b)");
    }

    @Test
    public void testCdr() {
        test("(cdr '(1 2 3))", "(2 3)");
        test("(cdr '((a b) c d))", "(c d)");
    }

    @Test
    public void testIf() {
        test("(if true 1 2)", "1");
        test("(if false 1 2)", "2");
    }

    @Test
    public void testAddition() {
        test("(+ 2 3 5)", "10");
    }

    @Test
    public void testNestedExpressions() {
        test("(car (cdr '(0 1 2 3)))", "1");
        test("(+ (car '(2 3)) (car '(4 5)))", "6");
    }

    @Test
    public void testCons() {
        test("(cons 1 '(2 3))", "(1 2 3)");
    }

    @Test
    public void testDefAndSet() {
        test("(def x 10)", "10");
        test("(set x 20)", "20");
        test("x", "20");
    }

    @Test
    public void testSymbol() {
        test("(symbol \"foo\")", "foo");
        test("(symbol \"bar\")", "bar");
    }

    @Test
    public void testDo() {
        test("(do 1 2 3)", "3");
        test("(do (def x 5) (* 2 2) (- 5 2))", "3");
        test("x", "5");
    }

    @Test
    public void testTypeOf() {
        test("(typeof 1)", "(\"int\")");
        test("(typeof 1.5)", "(\"double\")");
        test("(typeof true)", "(\"boolean\")");
        test("(typeof '())", "(\"list\")");
        test("(typeof \"hello\")", "(\"string\")");
    }

    @Test
    public void testArithmeticAndComparison() {
        test("(+ 1 2 3)", "6");
        test("(- 10 4 3)", "3");
        test("(* 2 3 4)", "24");
        test("(/ 20 2 2)", "5");
        test("(% 10 3)", "1");

        test("(> 5 2)", "true");
        test("(< 1 2)", "true");
        test("(= 4 4)", "true");
        test("(!= 4 5)", "true");
    }

    @Test
    public void testListManipulations() {
        test("(cons 0 '(1 2 3))", "(0 1 2 3)");
        test("(car (cdr '(10 20 30)))", "20");
        test("(cdr (cdr '(10 20 30)))", "(30)");
    }

    @Test
    public void testNested() {
        test("(+ (car '(1 2)) (car '(3 4)))", "4");
        test("(car (cdr (cons 0 '(1 2 3))))", "1");
    }

    @Test
    public void testComplexExpressions() {
        test("(+ (* 2 3) (- 10 4) (/ 8 2))", "16"); // 6 + 6 + 4 = 16

        test("(car (cdr (cdr '(a b c d e))))", "c");

        test("(cdr (cons 0 '(1 2 3)))", "(1 2 3)");

        test("(if (> 5 2) (+ 1 1) (* 2 2))", "2");
        test("(if (< 5 2) (+ 1 1) (* 2 2))", "4");

        test("(do (def x 10) (set x (+ x 5)) x)", "15");

        test("(do (def y 42) (symbol \"y\") y)", "42");

        test("(car (cdr (cons '(1 2) (cons '(3 4) '(5 6)))))", "(3 4)");

        test("(+ 1 2 (* 2 3) (/ 8 2) (- 10 5))", "18"); // 1+2+6+4+5=18

        test("(car (cdr (cdr '(10 20 30 40))))", "30");
        test("(cdr (cdr (cdr '(1 2 3 4 5))))", "(4 5)");

        test("(do (def lst '(a b)) (cons '(x y) lst))", "((x y) a b)");
    }

    @Test
    public void testFactorial1() {
        test("  (def n 5)\n" +
                "  (def res 1)\n" +
                "  (def f '(do\n" +
                "        (set res (* res n))\n" +
                "        (set n (- n 1))\n" +
                "        (if (< 0 n) (eval f) 0)\n" +
                "    ))\n" +
                "  (eval f)\n" +
                " ", "120");
    }

    @Test
    public void testFactorial2() {
        test("  (def n 7)\n" +
                "  (def res 1)\n" +
                "  (def f '(do\n" +
                "        (set res (* res n))\n" +
                "        (set n (- n 1))\n" +
                "        (if (< 0 n) (eval f) 0)\n" +
                "    ))\n" +
                "  (eval f)\n" +
                " ", "5040");
    }

    @Test
    public void testFactorial3() {
        test("  (def n 3)\n" +
                "  (def res 1)\n" +
                "  (def f '(do\n" +
                "        (set res (* res n))\n" +
                "        (set n (- n 1))\n" +
                "        (if (< 0 n) (eval f) 0)\n" +
                "    ))\n" +
                "  (eval f)\n" +
                " ", "6");
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
}
