package com.xf.js2;
import java.util.ArrayList;
import java.util.List;

//Lexer（词法分析器）
class Lexer {
	private String input;
    private int pos = 0;
	private char currentChar;

	public Lexer(String input) {
		this.input = input;
		this.currentChar = input.charAt(pos);
	}

	private void advance() {
	     pos++;
	     if (pos >= input.length()) {
	         currentChar = '\0';
	     } else {
	         currentChar = input.charAt(pos);
	     }
	}

 private void skipWhitespace() {
     while (currentChar != '\0' && Character.isWhitespace(currentChar)) {
         advance();
     }
 }

 private String integer() {
     StringBuilder result = new StringBuilder();
     while (currentChar != '\0' && Character.isDigit(currentChar)) {
         result.append(currentChar);
         advance();
     }
     return result.toString();
 }

 private String identifier() {
     StringBuilder result = new StringBuilder();
     while (currentChar != '\0' && (Character.isLetterOrDigit(currentChar) || currentChar == '_')) {
         result.append(currentChar);
         advance();
     }
     return result.toString();
 }

 public List<Token> tokenize() {
     List<Token> tokens = new ArrayList<>();

     while (currentChar != '\0') {
         if (Character.isWhitespace(currentChar)) {
             skipWhitespace();
             continue;
         }

         if (Character.isDigit(currentChar)) {
             tokens.add(new Token(TokenType.NUMBER, integer()));
             continue;
         }

         if (Character.isLetter(currentChar)) {
             String id = identifier();
             if (id.equals("let")) {
                 tokens.add(new Token(TokenType.LET, id));
             } else if (id.equals("print")) {
                 tokens.add(new Token(TokenType.PRINT, id));
             } else {
                 tokens.add(new Token(TokenType.IDENTIFIER, id));
             }
             continue;
         }

         switch (currentChar) {
             case '=':
                 tokens.add(new Token(TokenType.EQUALS, "="));
                 break;
             case '+':
                 tokens.add(new Token(TokenType.PLUS, "+"));
                 break;
             case '-':
                 tokens.add(new Token(TokenType.MINUS, "-"));
                 break;
             case '*':
                 tokens.add(new Token(TokenType.MULTIPLY, "*"));
                 break;
             case '/':
                 tokens.add(new Token(TokenType.DIVIDE, "/"));
                 break;
             case '(':
                 tokens.add(new Token(TokenType.LPAREN, "("));
                 break;
             case ')':
                 tokens.add(new Token(TokenType.RPAREN, ")"));
                 break;
             case ';':
                 tokens.add(new Token(TokenType.SEMICOLON, ";"));
                 break;
             case '<':
                 tokens.add(new Token(TokenType.LESS_THAN, "<"));
                 break;
             case '>':
                 tokens.add(new Token(TokenType.GREATER_THAN, ">"));
                 break;
             default:
                 throw new RuntimeException("Unknown character: " + currentChar);
         }
         advance();
     }

     tokens.add(new Token(TokenType.EOF, ""));
     return tokens;
 }
}
