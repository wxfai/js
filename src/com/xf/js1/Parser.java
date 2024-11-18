package com.xf.js1;
import java.util.ArrayList;
import java.util.List;

class Parser {
    private List<Token> tokens;
    private int pos = 0;

    private Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public static ASTNode parse(List<Token> tokens) {
        Parser parser = new Parser(tokens);
        ASTNode ast = parser.parse();
        return ast;
    }

    private Token currentToken() {
    	Token token = tokens.get(pos);
        return token;
    }

    private void next() {
    	Token token = tokens.get(pos);
    	System.out.println("currentToken: "+ token.index + ", " + token.value);
        pos++;
    }

    private void expect(TokenType type) {
    	Token token = currentToken();
    	if (token.type != type) {
            throw new RuntimeException("Expected token: " + type + " but got: " + token.type);
        }
        next();
    }

    private ASTNode parse() {
    	List<ASTNode> statements = new ArrayList<>();

        // 逐条解析指令，直到解析完所有语句
        while (currentToken().type != TokenType.EOF) {
            statements.add(parseStatement());
        }

        return new BlockNode(statements); // 返回一个包含所有指令的 BlockNode
    }
    
    private ASTNode parseStatement() {
    	Token token = currentToken();
        if (token.type == TokenType.LET) {
            return parseVariableDeclaration();
        } else if (token.type == TokenType.PRINT) {
            return parsePrintStatement();
        } else if (token.type == TokenType.IF) {
            return parseIfStatement();
		} else if (token.type == TokenType.FOR) {
            return parseForStatement();
		} else if (token.type == TokenType.IDENTIFIER) {
			return parseAssignmentStatement();

        }
        throw new RuntimeException("Unexpected token: " + token.type);
    }
    private ASTNode parseAssignmentStatement() {
        String varName = currentToken().value;
        expect(TokenType.IDENTIFIER);
        expect(TokenType.ASSIGN);
        ASTNode expression = parseExpression();
        expect(TokenType.SEMICOLON);
        return new AssignmentNode(varName, expression);
    }
    private ASTNode parseVariableDeclaration() {
        next(); // skip 'let'
        String varName = currentToken().value;
        expect(TokenType.IDENTIFIER);
        expect(TokenType.ASSIGN);
        ASTNode expression = parseExpression();
        expect(TokenType.SEMICOLON);
        return new VariableDeclarationNode(varName, expression);
    }

    private ASTNode parsePrintStatement() {
        next(); // skip 'print'
        expect(TokenType.LPAREN);
        List<ASTNode> expressions = new ArrayList<>();
        expressions.add(parseExpression());
        while (currentToken().type == TokenType.COMMA) {
            next(); // skip ','
            expressions.add(parseExpression());
        }
        expect(TokenType.RPAREN);
        expect(TokenType.SEMICOLON);
        return new PrintNode(expressions);
    }

    private ASTNode parseIfStatement() {
        next(); // skip 'if'
        expect(TokenType.LPAREN);
        ASTNode condition = parseExpression();
        expect(TokenType.RPAREN);
        ASTNode ifBlock = parseBlock();

        ASTNode elseBlock = null;
        if (currentToken().type == TokenType.ELSE) {
            next(); // skip 'else'
            elseBlock = parseBlock();
        }

        return new IfNode(condition, ifBlock, elseBlock);
    }
	private ASTNode parseForStatement() {
        next(); // skip 'for'
        expect(TokenType.LPAREN);
        ASTNode init = parseVariableDeclaration();
//        expect(TokenType.SEMICOLON);
        ASTNode condition = parseExpression();
        expect(TokenType.SEMICOLON);
        ASTNode update = parseStatement(); //parseExpression();
        expect(TokenType.RPAREN);
        ASTNode body = parseBlock();
        return new ForNode(init, condition, update, body);
    }
    private ASTNode parseBlock() {
        expect(TokenType.LBRACE);
        List<ASTNode> statements = new ArrayList<>();
        while (currentToken().type != TokenType.RBRACE) {
            statements.add(parseStatement());
        }
        expect(TokenType.RBRACE);
        return new BlockNode(statements);
    }
    
    private ASTNode parseExpression() {
        ASTNode left = parseTerm();
        Token token = currentToken();
        while(token.type == TokenType.PLUS || 
    		token.type == TokenType.MINUS || 
            token.type == TokenType.MULTIPLY || 
            token.type == TokenType.DIVIDE || 
            token.type == TokenType.LESS_THAN || 
            token.type == TokenType.GREATER_THAN) {
            String operator = token.value;
            next();
            ASTNode right = parseTerm();
            left = new BinaryOpNode(left, operator, right);
            token = currentToken();
        }
        return left;
    }

    private ASTNode parseTerm() {
    	Token token = currentToken();
        if (token.type == TokenType.NUMBER) {
            ASTNode numberNode = new NumberNode(token.value);
            next();
            return numberNode;
        } else if (token.type == TokenType.STRING) {
            ASTNode stringNode = new StringNode(token.value);
            next();
            return stringNode;
        } else if (token.type == TokenType.LPAREN) {
            next();
            ASTNode expression = parseExpression();
            expect(TokenType.RPAREN);
            return expression;
        } else if (token.type == TokenType.IDENTIFIER) { // i = i+1;
            //next();
            //ASTNode node = new VariableDeclarationNode(consume(TokenType.IDENTIFIER).value, null); // 暂时未处理
            ASTNode expression = null;
//            expression = parseStatement();

            ASTNode node = new VariableDeclarationNode(token.value, expression); // 暂时未处理
            next();
            return node;
        }
        throw new RuntimeException("Unexpected token: " + token.type);
    }
}

