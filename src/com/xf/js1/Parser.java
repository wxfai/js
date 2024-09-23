package com.xf.js1;
import java.util.ArrayList;
import java.util.List;

class Parser {
    private List<Token> tokens;
    private int pos = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token currentToken() {
        return tokens.get(pos);
    }

    private void advance() {
        pos++;
    }

    private void expect(TokenType type) {
        if (currentToken().type != type) {
            throw new RuntimeException("Expected token: " + type + " but got: " + currentToken().type);
        }
        advance();
    }

    public ASTNode parse() {
    	List<ASTNode> statements = new ArrayList<>();

        // 逐条解析指令，直到解析完所有语句
        while (currentToken().type != TokenType.EOF) {
            statements.add(parseStatement());
        }

        return new BlockNode(statements); // 返回一个包含所有指令的 BlockNode
    }
    
    private ASTNode parseStatement() {
        if (currentToken().type == TokenType.LET) {
            return parseVariableDeclaration();
        } else if (currentToken().type == TokenType.PRINT) {
            return parsePrintStatement();
        }
        throw new RuntimeException("Unexpected token: " + currentToken().type);
    }

    private ASTNode parseVariableDeclaration() {
        advance(); // skip 'let'
        String varName = currentToken().value;
        expect(TokenType.IDENTIFIER);
        expect(TokenType.EQUALS);
        ASTNode expression = parseExpression();
        expect(TokenType.SEMICOLON);
        return new VariableDeclarationNode(varName, expression);
    }

    private ASTNode parsePrintStatement() {
        advance(); // skip 'print'
        expect(TokenType.LPAREN);
        ASTNode expression = parseExpression();
        expect(TokenType.RPAREN);
        expect(TokenType.SEMICOLON);
        return new PrintNode(expression);
    }

    private ASTNode parseExpression() {
        ASTNode left = parseTerm();
        while ( currentToken().type == TokenType.PLUS || 
        		currentToken().type == TokenType.MINUS|| 
                currentToken().type == TokenType.LESS_THAN || 
                currentToken().type == TokenType.GREATER_THAN) {
            String operator = currentToken().value;
            advance();
            ASTNode right = parseTerm();
            left = new BinaryOpNode(left, operator, right);
        }
        return left;
    }

    private ASTNode parseTerm() {
    	Token token = currentToken();
        if (token.type == TokenType.NUMBER) {
            ASTNode numberNode = new NumberNode(token.value);
            advance();
            return numberNode;
        } else if (token.type == TokenType.LPAREN) {
            advance();
            ASTNode expression = parseExpression();
            expect(TokenType.RPAREN);
            return expression;
        } else if (token.type == TokenType.IDENTIFIER) {
            //advance();
            //ASTNode node = new VariableDeclarationNode(consume(TokenType.IDENTIFIER).value, null); // 暂时未处理

            ASTNode node = new VariableDeclarationNode(token.value, null); // 暂时未处理
            advance();
            return node;
        }
        throw new RuntimeException("Unexpected token: " + currentToken().type);
    }
}

