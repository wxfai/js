package com.xf.js2;
import java.util.Map;

class VariableDeclarationNode extends ASTNode {
	String varName;
	ASTNode expression;
	
	public VariableDeclarationNode(String varName, ASTNode expression) {
	    this.varName = varName;
	    this.expression = expression;
	}
	
    @Override
    public Object evaluate(Map<String, Object> context) {
    	if(expression != null) {
	        Object value = expression.evaluate(context);
	        context.put(varName, value);
	        return value;
    	}
    	else {
    		Object value = context.get(this.varName);
    		return value;
    	}
    }
}
