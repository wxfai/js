package com.xf.js2;
import java.util.Map;

class BinaryOpNode extends ASTNode {
	ASTNode left;
	String operator;
	ASTNode right;

	public BinaryOpNode(ASTNode left, String operator, ASTNode right) {
	    this.left = left;
	    this.operator = operator;
	    this.right = right;
	}

    @Override
    public Object evaluate(Map<String, Object> context) {
        Object leftValue = left.evaluate(context);
        Object rightValue = right.evaluate(context);

        if (leftValue instanceof Integer && rightValue instanceof Integer) {
            int leftInt = (int) leftValue;
            int rightInt = (int) rightValue;

            switch (operator) {
                case "+":
                    return leftInt + rightInt;
                case "-":
                    return leftInt - rightInt;
                case "*":
                    return leftInt * rightInt;
                case "/":
                    if (rightInt == 0) throw new ArithmeticException("Division by zero");
                    return leftInt / rightInt;
                case "<":
                    return leftInt < rightInt;
                case ">":
                    return leftInt > rightInt;
                default:
                    throw new RuntimeException("Unknown operator: " + operator);
            }
        }
        throw new RuntimeException("Unsupported types for operator: " + operator);
    }
}
