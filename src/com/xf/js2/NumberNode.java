package com.xf.js2;
import java.util.Map;

class NumberNode extends ASTNode {
    String value;

    public NumberNode(String value) {
        this.value = value;
    }

    @Override
    public Object evaluate(Map<String, Object> context) {
        return Integer.parseInt(value);
    }
}