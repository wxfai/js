package com.xf.js1;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main2(String[] args) {
        String code = "let x=5; let y = 10; let z = x + y; print(x); if(y>x){print(z);}";
        code = "let x = 10 < 20; let y = 30 > 15; print(x); print(y);";
        code = "let x = 10 ; let y = 30 ; print(x); print(y);";

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();
        tokens.forEach(System.out::println);

        Parser parser = new Parser(tokens);
        ASTNode ast = parser.parse();
        
        // 创建执行上下文 (保存变量的哈希表)
        Map<String, Object> context = new HashMap<>();

        // 执行 AST
        ast.evaluate(context);
        System.out.println("AST generated: " + ast);
    }
    public static void main(String[] args) {
        // 示例代码
        String code = "let x = 10 + 20; let y = x > 15; print(x); print(y);";
        code = "let x = 10 < 20; let y = 30 > 15; print(x); print(y);";
        code = "let x = 10 ; let y = 30 ; print(x); print(y);";

        // 词法分析
        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();
        tokens.forEach(System.out::println);

        // 语法解析
        Parser parser = new Parser(tokens);
        ASTNode ast = parser.parse();

        // 创建执行上下文 (保存变量的哈希表)
        Map<String, Object> context = new HashMap<>();

        // 执行 AST
        ast.evaluate(context);

        // 输出上下文变量的内容
        System.out.println("Execution context: " + context);
    }
}

