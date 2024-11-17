package com.xf.js1;
import java.util.Map;

class StringNode extends ASTNode {
    String value;

    public StringNode(String value) {
        this.value = value;
    }

    @Override
    public Object evaluate(Map<String, Object> context) {
        return value;
    }
}