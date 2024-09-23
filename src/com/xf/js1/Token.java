package com.xf.js1;

//标记类型
enum TokenType {
	LET, IF, FOR, PRINT, 
	IDENTIFIER, NUMBER, 
	EQUALS, LESS_THAN, GREATER_THAN, PLUS, MINUS, MULTIPLY, DIVIDE, 
	LPAREN, RPAREN, LBRACE, RBRACE, 
	SEMICOLON, 
	EOF // End of file
}

//标记类
class Token {
	TokenType type;
	String value;
	
	Token(TokenType type, String value) {
	    this.type = type;
	    this.value = value;
	}
	
	@Override
	public String toString() {
	    return String.format("Token(%s, '%s')", type, value);
	}
}

