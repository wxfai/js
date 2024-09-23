package com.xf.js2;
import java.util.ArrayList;
import java.util.List;

// Parser（语法分析器）
class Parser {
    private List<Token> tokens;
    private int position;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
    }

    private Token currentToken() {
        return tokens.get(position);
    }

    private Token consume(TokenType type) {
        Token token = currentToken();
        if (token.type != type) {
            throw new RuntimeException("Expected token " + type + " but found " + token.type);
        }
        position++;
        return token;
    }

    public ASTNode parse() {
        List<ASTNode> statements = new ArrayList<>();

        while (currentToken().type != TokenType.EOF) {
            statements.add(parseStatement());
        }

        return new BlockNode(statements);
    }

    private ASTNode parseStatement() {
        if (currentToken().type == TokenType.LET) {
            return parseVariableDeclaration();
        } else if (currentToken().type == TokenType.PRINT) {
            return parsePrintStatement();
        } else {
            return parseExpressionStatement();
        }
    }

    private ASTNode parseVariableDeclaration() {
        consume(TokenType.LET);
        String varName = consume(TokenType.IDENTIFIER).value;
        consume(TokenType.EQUALS);
        ASTNode expression = parseExpression();
        consume(TokenType.SEMICOLON);
        return new VariableDeclarationNode(varName, expression);
    }

    private ASTNode parsePrintStatement() {
        consume(TokenType.PRINT);
        consume(TokenType.LPAREN);
        ASTNode expression = parseExpression();
        consume(TokenType.RPAREN);
        consume(TokenType.SEMICOLON);
        return new PrintNode(expression);
    }

    private ASTNode parseExpressionStatement() {
        ASTNode expression = parseExpression();
        consume(TokenType.SEMICOLON);
        return expression;
    }

    private ASTNode parseExpression() {
        return parseTerm();
    }

    private ASTNode parseTerm() {
        ASTNode left = parseFactor();

        while (currentToken().type == TokenType.PLUS || currentToken().type == TokenType.MINUS ||
               currentToken().type == TokenType.LESS_THAN || currentToken().type == TokenType.GREATER_THAN) {
            String operator = currentToken().value;
            consume(currentToken().type);
            ASTNode right = parseFactor();
            left = new BinaryOpNode(left, operator, right);
        }
        return left;
    }

    private ASTNode parseFactor() {
        if (currentToken().type == TokenType.NUMBER) {
            return new NumberNode(consume(TokenType.NUMBER).value);
        } else if (currentToken().type == TokenType.IDENTIFIER) {
            return new VariableDeclarationNode(consume(TokenType.IDENTIFIER).value, null); // 暂时未处理
        } else if (currentToken().type == TokenType.LPAREN) {
            consume(TokenType.LPAREN);
            ASTNode expr = parseExpression();
            consume(TokenType.RPAREN);
            return expr;
        }
        throw new RuntimeException("Unexpected token: " + currentToken().type);
    }
}
