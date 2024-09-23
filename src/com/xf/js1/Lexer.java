package com.xf.js1;
import java.util.ArrayList;
import java.util.List;

class Lexer {
    private final String input;
    private int pos = 0;
    private char currentChar;

    public Lexer(String input) {
        this.input = input;
        this.currentChar = input.charAt(pos);
    }

    private void advance() {
        pos++;
        if (pos >= input.length()) {
            currentChar = '\0';  // Null character to represent EOF
        } else {
            currentChar = input.charAt(pos);
        }
    }

    private void skipWhitespace() {
        while (currentChar != '\0' && Character.isWhitespace(currentChar)) {
            advance();
        }
    }

    private String consumeIdentifier() {
        StringBuilder result = new StringBuilder();
        while (currentChar != '\0' && (Character.isLetterOrDigit(currentChar) || currentChar == '_')) {
            result.append(currentChar);
            advance();
        }
        return result.toString();
    }

    private String consumeNumber() {
        StringBuilder result = new StringBuilder();
        while (currentChar != '\0' && Character.isDigit(currentChar)) {
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

            if (Character.isLetter(currentChar)) {
                String identifier = consumeIdentifier();
                switch (identifier) {
                    case "let":
                        tokens.add(new Token(TokenType.LET, identifier));
                        break;
                    case "if":
                        tokens.add(new Token(TokenType.IF, identifier));
                        break;
                    case "print":
                        tokens.add(new Token(TokenType.PRINT, identifier));
                        break;
                    default:
                        tokens.add(new Token(TokenType.IDENTIFIER, identifier));
                        break;
                }
                continue;
            }

            if (Character.isDigit(currentChar)) {
                tokens.add(new Token(TokenType.NUMBER, consumeNumber()));
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
                case '{':
                    tokens.add(new Token(TokenType.LBRACE, "{"));
                    break;
                case '}':
                    tokens.add(new Token(TokenType.RBRACE, "}"));
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

