package com.xf.js1;
import java.util.Map;

class IfNode extends ASTNode {
    ASTNode condition;
    ASTNode ifBlock;
    ASTNode elseBlock;

    public IfNode(ASTNode condition, ASTNode ifBlock, ASTNode elseBlock) {
        this.condition = condition;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }

    @Override
    public Object evaluate(Map<String, Object> context) {
        boolean conditionResult = (boolean) condition.evaluate(context);
        if (conditionResult) {
            return ifBlock.evaluate(context);
        } else if (elseBlock != null) {
            return elseBlock.evaluate(context);
        }
        return null;
    }
}