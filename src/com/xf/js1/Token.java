package com.xf.js1;
//标记类型
enum TokenType {
	LET,
	ASSIGN, 
	IF, 
	ELSE, 
	FOR, 
	PRINT, 
	IDENTIFIER, 
	NUMBER, 
	STRING,
	EQUALS, 		// =
	LESS_THAN, 		// <
	GREATER_THAN,	// >
	PLUS, 			// +
	MINUS, 			// -
	MULTIPLY, 		// *
	DIVIDE, 		// /
	LPAREN, 		// (
	RPAREN, 		// )
	LBRACE, 		// {
	RBRACE, 		// }
	SEMICOLON,		// ;
	COMMA,			// ,
	EOF 			// End of file
}

//标记类
class Token {
	TokenType type;
	String value;
	int index;
	static int g_index = 0;
	
	Token(TokenType type, String value) {
	    this.type = type;
	    this.value = value;
	    this.index = g_index;
	    g_index ++;
	}
	
	@Override
	public String toString() {
	    return String.format("%d Token(%s, '%s')", index, type, value);
	}
}

