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

    private void next() {
        pos++;
        if (pos >= input.length()) {
            currentChar = '\0';  // Null character to represent EOF
        } else {
            currentChar = input.charAt(pos);
        }
    }

    private void skipWhitespace() {
        while (currentChar != '\0' && Character.isWhitespace(currentChar)) {
            next();
        }
    }

    private String identifier() {
        StringBuilder result = new StringBuilder();
        while (currentChar != '\0' && (Character.isLetterOrDigit(currentChar) || currentChar == '_')) {
            result.append(currentChar);
            next();
        }
        String id = result.toString();
        return id;
    }

    private String parseNumber() {
        StringBuilder result = new StringBuilder();
        while (currentChar != '\0' && Character.isDigit(currentChar)) {
            result.append(currentChar);
            next();
        }
        return result.toString();
    }
    private String consumeString() {
        StringBuilder result = new StringBuilder();
        next(); // skip the opening quote
        while (currentChar != '\0' && currentChar != '"') {
            result.append(currentChar);
            next();
        }
        if (currentChar != '"') {
            throw new RuntimeException("Unterminated string");
        }
        next(); // skip the closing quote
        return result.toString();
    }
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (currentChar != '\0') {
            if (Character.isWhitespace(currentChar)) {
                skipWhitespace();
                continue;
            }

            if (currentChar == '"') {
                tokens.add(new Token(TokenType.STRING, consumeString()));
                continue;
            }

            if (Character.isLetter(currentChar)) {
                String identifier = identifier();
                switch (identifier) {
                    case "let":
                        tokens.add(new Token(TokenType.LET, identifier));
                        break;
                    case "if":
                        tokens.add(new Token(TokenType.IF, identifier));
                        break;
                    case "else":
                        tokens.add(new Token(TokenType.ELSE, identifier));
                        break;
                    case "for":
                        tokens.add(new Token(TokenType.FOR, identifier));
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
                tokens.add(new Token(TokenType.NUMBER, parseNumber()));
                continue;
            }

            switch (currentChar) {
                case '=':
                    next();
                    if (currentChar == '=') {
                        tokens.add(new Token(TokenType.EQUALS, "=="));
                        next();
                    } else {
                        pos--;
                        tokens.add(new Token(TokenType.ASSIGN, "="));
                    }
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
                case ',':
                    tokens.add(new Token(TokenType.COMMA, ","));
                    break;
                default:
                    throw new RuntimeException("Unknown character: " + currentChar);
            }
            next();
        }

        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }
}

