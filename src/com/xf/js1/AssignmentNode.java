package com.xf.js1;
import java.util.Map;

class AssignmentNode extends ASTNode {
    String varName;
    ASTNode expression;

    public AssignmentNode(String varName, ASTNode expression) {
        this.varName = varName;
        this.expression = expression;
    }

    @Override
    public Object evaluate(Map<String, Object> context) {
        Object value = expression.evaluate(context);
        context.put(varName, value);
        return value;
    }
}