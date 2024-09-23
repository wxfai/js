package com.xf.js1;
import java.util.List;
import java.util.Map;

public class BlockNode extends ASTNode {
    List<ASTNode> statements;

    public BlockNode(List<ASTNode> statements) {
        this.statements = statements;
    }

    @Override
    public Object evaluate(Map<String, Object> context) {
        Object lastResult = null;
        for (ASTNode statement : statements) {
            lastResult = statement.evaluate(context); // 顺序执行每条语句
        }
        return lastResult;
    }
}
