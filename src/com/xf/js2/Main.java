package com.xf.js2;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Main 类
public class Main {
	public static void main(String[] args) {
		String code;
		code = "let x = 10 + 20; let y = x > 15; print(x); print(y);";
//		code =  "let x = 10;";
//		code += "let y = 15;";
//		code += "print(x);";
//		code += "print(y);";

		// 词法分析
		Lexer lexer = new Lexer(code);
		List<Token> tokens = lexer.tokenize();
		tokens.forEach(System.out::println);
		
		// 语法解析
		Parser parser = new Parser(tokens);
		ASTNode program = parser.parse();
		
		// 创建执行上下文
		Map<String, Object> context = new HashMap<>();
		
		// 执行 AST
		program.evaluate(context);
		
		// 输出上下文变量的内容
		System.out.println("Execution context: " + context);
	}
}
