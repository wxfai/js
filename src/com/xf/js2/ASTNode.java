package com.xf.js2;
import java.util.Map;

// AST 节点类
abstract class ASTNode {
    abstract Object evaluate(Map<String, Object> context);
}


