package com.xf.js1;
import java.util.Map;

class ForNode extends ASTNode {
    ASTNode init;
    ASTNode condition;
    ASTNode update;
    ASTNode body;

    public ForNode(ASTNode init, ASTNode condition, ASTNode update, ASTNode body) {
        this.init = init;
        this.condition = condition;
        this.update = update;
        this.body = body;
    }

    @Override
    public Object evaluate(Map<String, Object> context) {
        init.evaluate(context);
        while ((boolean) condition.evaluate(context)) {
            body.evaluate(context);
            update.evaluate(context);
        }
        return null;
    }
}