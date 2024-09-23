package com.xf.js;

import java.util.HashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.Map;

public class js {
    // 存储变量
    private Map<String, Object> variables = new HashMap<>();

    public static void main(String[] args) {
        js engine = new js();

        String code = "let x = 5; let y = 10; if (x < y) { print(x); let z = x + y; print(z); }";
        
        engine.interpret(code); // 输出: 5 15
    }

    // 解释并执行代码
    private void interpret(String code) {
        int length = code.length();
        StringBuilder currentStatement = new StringBuilder();
        int braceDepth = 0; // 记录花括号嵌套的层数

        for (int i = 0; i < length; i++) {
            char c = code.charAt(i);

            if (c == '{') {
                braceDepth++;
                currentStatement.append(c);
            } else if (c == '}') {
                braceDepth--;
                currentStatement.append(c);

                if (braceDepth == 0) {
                    execute(currentStatement.toString().trim());
                    currentStatement.setLength(0); // 清空当前语句
                }
            } else if (c == ';' && braceDepth == 0) {
                // 遇到分号并且不在花括号内部时，认为一条语句结束
                execute(currentStatement.toString().trim());
                currentStatement.setLength(0); // 清空当前语句
            } else {
                currentStatement.append(c);
            }
        }

        // 如果有剩余语句，执行它
        if (currentStatement.length() > 0) {
            execute(currentStatement.toString().trim());
        }
    }
    
    private void execute(String line) {
        if (line.startsWith("let ")) {
            // 变量声明: let x = 5
            String[] parts = line.substring(4).split("=");
            if (parts.length != 2) {
                throw new RuntimeException("Syntax error in variable declaration.");
            }
            String varName = parts[0].trim();
            Object value = evaluateExpression(parts[1].trim());
            variables.put(varName, value);
        } else if (line.startsWith("if ")) {
            // 条件语句: if (x < 10) { print(x); let z = x + 10; }
            int conditionStart = line.indexOf('(');
            int conditionEnd = line.indexOf(')');
            if (conditionStart == -1 || conditionEnd == -1 || conditionEnd <= conditionStart) {
                throw new RuntimeException("Syntax error in if condition.");
            }

            // 提取条件
            String condition = line.substring(conditionStart + 1, conditionEnd).trim();

            // 提取 if 后的代码块内容
            int blockStart = line.indexOf('{');
            if (blockStart == -1) {
                throw new RuntimeException("Syntax error: Missing '{' in if statement.");
            }
            int blockEnd = findClosingBrace(line, blockStart);
            if (blockEnd == -1) {
            	blockEnd = line.length();
//                throw new RuntimeException("Syntax error: Missing '}' in if statement.");
            }

            String body = line.substring(blockStart + 1, blockEnd).trim();

            // 如果条件成立，执行代码块
            if (evaluateCondition(condition)) {
                interpret(body);
            }
        } else if (line.startsWith("print(")) {
            // 输出变量: print(x)
            String varName = line.substring(6, line.length() - 1).trim();
            if (variables.containsKey(varName)) {
                System.out.println(variables.get(varName));
            } else {
                throw new RuntimeException("Undefined variable: " + varName);
            }
        } else if (line.contains("=")) {
            // 变量赋值: x = 10
            String[] parts = line.split("=");
            if (parts.length != 2) {
                throw new RuntimeException("Syntax error in assignment.");
            }
            String varName = parts[0].trim();
            Object value = evaluateExpression(parts[1].trim());
            if (variables.containsKey(varName)) {
                variables.put(varName, value);
            } else {
                throw new RuntimeException("Variable not declared: " + varName);
            }
        } else {
            throw new RuntimeException("Syntax error: " + line);
        }
    }

    // 查找匹配的闭合花括号
    private int findClosingBrace(String code, int openBraceIndex) {
        int braceCount = 0;
        for (int i = openBraceIndex; i < code.length(); i++) {
            if (code.charAt(i) == '{') {
                braceCount++;
            } else if (code.charAt(i) == '}') {
                braceCount--;
            }

            if (braceCount == 0) {
                return i;
            }
        }
        return -1; // 如果没有找到匹配的闭合括号，返回 -1
    }

    // 简单表达式求值
    private Object evaluateExpression(String expr) {
        expr = expr.replaceAll("\\s+", ""); // 移除空格
        if (expr.contains("+")) {
            String[] parts = expr.split("\\+");
            return (Integer)evaluateExpression(parts[0].trim()) + (Integer)evaluateExpression(parts[1].trim());
        } else if (expr.contains("-")) {
            String[] parts = expr.split("-");
            return (Integer)evaluateExpression(parts[0].trim()) - (Integer)evaluateExpression(parts[1].trim());
        } else {
            // 返回变量或常量
            if (variables.containsKey(expr)) {
                return (int) variables.get(expr); // 强制转换成整数
            } else {
                try {
                    return Integer.parseInt(expr); // 尝试解析为整数
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Invalid expression: " + expr);
                }
            }
        }
    }

    // 条件判断
    private boolean evaluateCondition(String condition) {
        condition = condition.replaceAll("\\s+", ""); // 移除空格
        if (condition.contains("<")) {
            String[] parts = condition.split("<");
            return (int) evaluateExpression(parts[0].trim()) < (int) evaluateExpression(parts[1].trim());
        } else if (condition.contains(">")) {
            String[] parts = condition.split(">");
            return (int) evaluateExpression(parts[0].trim()) > (int) evaluateExpression(parts[1].trim());
        } else if (condition.contains("==")) {
            String[] parts = condition.split("==");
            return (int) evaluateExpression(parts[0].trim()) == (int) evaluateExpression(parts[1].trim());
        } else {
            throw new RuntimeException("Invalid condition: " + condition);
        }
    }

}

