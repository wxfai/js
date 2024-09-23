package com.xf.js1;
import java.util.Map;

class PrintNode extends ASTNode {
	ASTNode expression;

	public PrintNode(ASTNode expression) {
	    this.expression = expression;
	}
	 
	@Override
	public Object evaluate(Map<String, Object> context) {
	    Object value = expression.evaluate(context);
	    System.out.println(value);
	    return value;
	}
}
