import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Interpreter {
    private HashMap<String, Integer> variables;
    private HashMap<String, List<String>> functions;

    public Interpreter() {
        this.variables = new HashMap<>();
        this.functions = new HashMap<>();
    }

    public void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your code below. Type 'execute' on a new line to run the code:\n>> ");
        String line; boolean cls = false;
        while (true) {
            if (cls){
                System.out.print(">> ");
                cls = false;
            }

            line = scanner.nextLine();
            if (line.trim().equals("exit")) {
                break;
            }
            if (line.trim().equals("clear")) {
                if (line.trim().equals("clear")) {
                    for (int i = 0; i < 50; i++) {// emptying
                        System.out.println();
                    }
                }
                cls = true;
            }
            if (cls)
                continue;

            runCode(line);
            System.out.print(">> ");
        }
    }

    private void runCode(String code) {
        String[] lines = code.split("\n");
        for (String line : lines) {
            executeLine(line.trim());
        }
    }

    private void executeLine(String line) {
        if (line.isEmpty()) {
            return;  // Skip empty lines
        }
        String[] tokens = line.split("\\s+");
        switch (tokens[0]) {
            case "var":
            case "VAR":
                executeVarDeclaration(tokens);
                break;
            case "print":
            case "PRINT":
                executePrint(tokens);
                break;
            case "if":
            case "IF":
                executeIf(Arrays.copyOfRange(tokens, 1, tokens.length));
                break;
            case "fun":
            case "FUN":
                executeFunction(tokens);
                break;
            default:
                System.out.println("Invalid statement: " + line);
        }
    }

    private void executeFunction(String[] tokens) {
        String functionName = tokens[1];
        List<String> functionVariables = new ArrayList<>();

        // Start from tokens[3], since tokens[2] is "%"
        for (int i = 3; i < tokens.length; i++) {
            String token = tokens[i];
            if (token.equals("%"))
                break;
            if (token.equals(","))
                continue;
            functionVariables.add(token);
        }
        // // Print the contents of functionVariables
        // System.out.println("Function Variables for " + functionName + ":");
        // for (String var : functionVariables) {
        //     System.out.println(var);
        // }
        functions.put(functionName, functionVariables);
    }

    private void executeVarDeclaration(String[] tokens) {
        if (tokens.length < 4 || !tokens[2].equals("=")) {
            System.out.println("Invalid variable declaration: " + String.join(" ", tokens));
            return;
        }
        String varName = tokens[1];
        try {
            int varValue = Integer.parseInt(tokens[3]);
            variables.put(varName, varValue);
        } catch (NumberFormatException e) {
            System.out.println("Invalid variable value: " + tokens[3]);
        }
    }

    private void executePrint(String[] tokens) {
        double num;
        if (tokens.length < 2) {
            System.out.println("Invalid print statement: " + String.join(" ", tokens));
            return;
        }
        String varName = tokens[1];

        if (variables.containsKey(varName)) {// needs to be a num for now
            int varValue = variables.get(varName);
            if (tokens.length > 2 && isMathExpression(tokens)) {
                double result = evaluateMathExpression(Arrays.copyOfRange(tokens, 2, tokens.length), varValue);
                System.out.println(result);
            } else {
                System.out.println(varValue);
            }
        } else {
            if (!isNum(tokens[1]))
                System.out.println("smt- we support only numbers for now: " + varName);
            else{
                if (tokens.length > 2 && isMathExpression(tokens)) {
                    num = strToDouble(tokens[1]);
                    double result = evaluateMathExpression(Arrays.copyOfRange(tokens, 2, tokens.length), num);
                    System.out.println(result);
                }
                else
                    System.out.println(tokens[1]);
            }

        }
    }

    private double strToDouble(String string) {
        return Double.parseDouble(string);
    }

    private boolean isNum(String value) {
        try {
            Float.parseFloat(value);
            return true;

        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void executeIf(String[] tokens) {
        if (tokens.length < 3) {
            System.out.println("Invalid if statement: " + String.join(" ", tokens));
            return;
        }

        String operator = tokens[1];
        int operand1;
        int operand2;

        try {
            operand1 = Integer.parseInt(tokens[0]);
            operand2 = Integer.parseInt(tokens[2]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid operands for operation: " + String.join(" ", tokens));
            return;
        }

        switch (operator) {
            case "<":
                System.out.println("Result: " + (operand1 < operand2));
                break;
            case ">":
                System.out.println("Result: " + (operand1 > operand2));
                break;
            case "=":
                System.out.println("Result: " + (operand1 == operand2));
                break;
            default:
                System.out.println("Invalid operator: " + operator);
                break;
        }
    }

    private boolean isMathExpression(String[] tokens) {
        for (String token : tokens) {
            if (token.matches("[+\\-*/]")) {
                return true;
            }
        }
        return false;
    }

    private double evaluateMathExpression(String[] expression, double initialValue) {
        double result = initialValue;
        String operator = null;
        for (String token : expression) {
            if (token.matches("[+\\-*/]")) {
                operator = token;
            } else {
                double operand;
                if (variables.containsKey(token)) {
                    operand = variables.get(token);
                } else {
                    try {
                        operand = Double.parseDouble(token);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid operand: " + token);
                        return 0;
                    }
                }
                switch (operator) {
                    case "+":
                        result += operand;
                        break;
                    case "-":
                        result -= operand;
                        break;
                    case "*":
                        result *= operand;
                        break;
                    case "/":
                        if (operand != 0) {
                            result /= operand;
                        } else {
                            System.out.println("Division by zero!");
                        }
                        break;
                    default:
                        System.out.println("Invalid operator: " + operator);
                        break;
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        Interpreter interpreter = new Interpreter();
        interpreter.execute();
    }
}
