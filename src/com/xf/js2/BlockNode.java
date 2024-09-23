package com.xf.js2;
import java.util.List;
import java.util.Map;

class BlockNode extends ASTNode {
    List<ASTNode> statements;

    public BlockNode(List<ASTNode> statements) {
        this.statements = statements;
    }

    @Override
    public Object evaluate(Map<String, Object> context) {
        Object lastResult = null;
        for (ASTNode statement : statements) {
            lastResult = statement.evaluate(context);
        }
        return lastResult;
    }
}

