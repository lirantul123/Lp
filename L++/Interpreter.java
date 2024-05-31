import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

// must write in spaces - for now
public class Interpreter {
    private HashMap<String, Integer> variables;

    public Interpreter() {
        this.variables = new HashMap<>();
    }

    public void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your code below. Type 'execute' on a new line to run the code:");
        String line;
        while (true) {
            line = scanner.nextLine();
            if (line.trim().equals("execute")) {
                break;
            }
            runCode(line); 
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
            case "var":// TODO: Run over changes
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
                executeFunction(Arrays.copyOfRange(tokens, 1, tokens.length));
                break;
            default:
                System.out.println("Invalid statement: " + line);
        }
    }
    
    private void executeFunction(String[] copyOfRange) {
        throw new UnsupportedOperationException("Unimplemented method 'executeFunction'");
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
        } catch (NumberFormatException e) {// Only numbers for now
            System.out.println("Invalid variable value: " + tokens[3]);
        }
    }

    private void executePrint(String[] tokens) {
        if (tokens.length < 2) {
            System.out.println("Invalid print statement: " + String.join(" ", tokens));
            return;
        }
        String varName = tokens[1];
        if (variables.containsKey(varName)) {
            int varValue = variables.get(varName);
            if (tokens.length > 2 && isMathExpression(tokens)) {
                int result = evaluateMathExpression(Arrays.copyOfRange(tokens, 2, tokens.length), varValue);
                System.out.println(result);
            } else {
                System.out.println(varValue);
            }
        } else {
            System.out.println("Variable not found: " + varName);
        }
    }

    private void executeIf(String[] tokens) {
        if (tokens.length < 3) {
            System.out.println("Invalid if statement: " + String.join(" ", tokens));
            return;
        }
        
        String operator = tokens[1];
        
        // Check if there are enough tokens to perform the operation
        // if (tokens.length < 4) {
        //     System.out.println("Insufficient operands for operation: " + operator);
        //     return;
        // }
        
        int operand1;
        int operand2;
        
        try {// need also to be implemneted by variables values(only number for now)
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
        // Check if the expression contains math operators
        for (String token : tokens) {
            if (token.matches("[+\\-*/]")) {
                return true;
            }
        }
        return false;
    }
    
    private int evaluateMathExpression(String[] expression, int initialValue) {
        int result = initialValue;
        String operator = null;
        for (String token : expression) {
            if (token.matches("[+\\-*/]")) {
                operator = token;
            } else {
                int operand;
                if (variables.containsKey(token)) {
                    operand = variables.get(token);
                } else {
                    try {
                        operand = Integer.parseInt(token);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid operand: " + token);
                        return 0; // or throw an exception
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
