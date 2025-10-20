package com.example.minilisp.repl;

import com.example.minilisp.lexer.Lexer;
import com.example.minilisp.parser.Expression;
import com.example.minilisp.parser.Parser;
import com.example.minilisp.tokens.Token;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;


public class Repl {

    public static void main(String[] args) {
        if (args.length > 1 && args[0].equals("--file")) {
            runFile(args[1]);
        } else {
            runInteractive();
        }
    }

    private static void runInteractive() {
        Scanner sc = new Scanner(System.in);
        String lastCommand = null;

        while (true) {
            StringBuilder inputBuilder = new StringBuilder();
            int parenCount = 0;

            System.out.print("MiniLisp> ");

            while (true) {
                String line = sc.nextLine().trim();

                if (line.equals(":q") || line.equals(":quit") || line.equals(":exit")) {
                    System.out.println("Bye!");
                    return;
                }

                if (line.startsWith(":l ") || line.startsWith(":load ")) {
                    String filename = line.substring(line.indexOf(' ') + 1).trim();
                    if (filename.isEmpty()) {
                        System.out.println("Error: no filename provided.");
                    } else {
                        runFile(filename);
                    }
                    break;
                }

                if (line.equals(":") && lastCommand != null) {
                    line = lastCommand;
                    System.out.println(line);
                }

                inputBuilder.append(line).append('\n');

                for (char c : line.toCharArray()) {
                    if (c == '(') parenCount++;
                    else if (c == ')') parenCount--;
                }

                if (parenCount <= 0 && inputBuilder.length() > 0) {
                    break;
                }

            }

            String code = inputBuilder.toString().trim();
            if (!code.isEmpty()) {
                lastCommand = code;
                execute(code);
            }
        }
    }


    private static void runFile(String filename) {
        try {
            String code = Files.readString(Path.of(filename));
            System.out.println("Executing file: " + filename);
            execute(code);
        } catch (IOException e) {
            System.out.println("Error: cannot read file " + filename);
        }
    }

    private static void execute(String code) {
        try {
            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.tokenize();
            Parser parser = new Parser(tokens);
            Expression expression = parser.parse();

            System.out.println("End");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
