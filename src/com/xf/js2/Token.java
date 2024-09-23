package com.xf.js2;

//Token 类型枚举
enum TokenType {
 LET, PRINT, IDENTIFIER, NUMBER, EQUALS, PLUS, MINUS, MULTIPLY, DIVIDE,
 LPAREN, RPAREN, LBRACE, RBRACE, SEMICOLON, EOF,
 LESS_THAN, GREATER_THAN
}

//Token 类
class Token {
 TokenType type;
 String value;

 public Token(TokenType type, String value) {
     this.type = type;
     this.value = value;
 }

 @Override
 public String toString() {
     return "Token(" + type + ", '" + value + "')";
 }
}
