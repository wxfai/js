package com.xf.js1;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String code = 
        		  "let x = 5; let h=\"hello\";"
        		+ "let y = 10;"
        		+ "let z = 0;let z=x + y+y;"
        		+ "print(h);"
        		+ "print(\"x=\",x);"
        		+ "if(y>x){print(\"z=\",z);}";
        code += "for (let i = 0; i < 5; i = i + 1;) {print(i); }";
        code += "let i = 0; i = i + 1;print(i);i = i + 1;print(i);";
        code = "let i = 2*3+1;print(i);";

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();
        tokens.forEach(Main::plog);
        plog("=========");

        Map<String, Object> context = new HashMap<>();

        ASTNode ast = Parser.parse(tokens);
        ast.evaluate(context);
        
        System.out.println("AST generated: " + ast);
        System.out.println("Execution context: " + context);
    }
    public static void plog(Object log) {
    	System.out.println(log);
    }
}

