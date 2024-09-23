package com.xf.js1;
import java.util.Map;

//AST 节点类型
public abstract class ASTNode {
	abstract Object evaluate(Map<String, Object> context);
}

