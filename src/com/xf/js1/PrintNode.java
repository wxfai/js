package com.xf.js1;
import java.util.List;
import java.util.Map;

class PrintNode extends ASTNode {
    List<ASTNode> expressions;

    public PrintNode(List<ASTNode> expressions) {
        this.expressions = expressions;
    }

    @Override
    public Object evaluate(Map<String, Object> context) {
        for (ASTNode expression : expressions) {
            Object value = expression.evaluate(context);
            System.out.print(value);
            System.out.print(" ");
        }
        System.out.println();
        return null;
    }
}
