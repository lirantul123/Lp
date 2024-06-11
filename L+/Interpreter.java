import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Interpreter {
    private static HashMap<String, Integer> variables;
    // [fun_nmae], [variables, content]
    private static HashMap<String, Map.Entry<List<String>, List<String>>> functions;

    public Interpreter() {
        Interpreter.variables = new HashMap<>();
        Interpreter.functions = new HashMap<>();
    }

    public void execute() throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            
            System.out.print("Enter your code below. Type 'clear' to clear the screen, 'exit' on a new line to exit:\n>> ");
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
    }

    private void runCode(String code) throws Exception {
        String[] lines = code.split("\n");
        for (String line : lines) {
            executeLine(line.trim(), false);
        }
    }

    private static void executeLine(String line, boolean innerFun)  throws Exception {
        if (line.isEmpty()) {
            return;
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
                if (!innerFun){
                    executeFunction(tokens);
                    break;
                }
                throw new Exception("Syntax Error: Function defenition cannot be inside one.");
            case "/":// comments
                break;
            default:
                // <fun_name> % <variables[,]> %
                executeExistFunction(tokens);
        }
    }

    private static void executeExistFunction(String[] tokens) throws Exception {
        String functionName = tokens[0];
        if (functions.containsKey(functionName)) {
            executeUserFunction(functionName, tokens);
        } else {
            System.out.println("Invalid statement or function not found: " + functionName);
        }
    }
    private static void executeUserFunction(String functionName, String[] tokens) throws Exception {
        if (tokens.length < 3 || !tokens[1].equals("%") || !tokens[tokens.length - 1].equals("%")) {
            throw new Exception("Syntax Error: Expected '%' to surround the function variables.");
        }

        String[] variableTokens = String.join(" ", Arrays.copyOfRange(tokens, 2, tokens.length - 1)).split("\\s*,\\s*");
        List<String> providedVariables = Arrays.asList(variableTokens);

        Map.Entry<List<String>, List<String>> function = functions.get(functionName);
        List<String> functionVariables = function.getKey();
        List<String> functionContent = function.getValue();

        // later to check types, but because it is python minus, maybe not
        if (providedVariables.size() != functionVariables.size()) {
            throw new Exception("Mismatching Argument: The number of provided variables does not match the function's variables.");
        }

        System.out.println("Executing function " + functionName + " with variables: " + providedVariables);
        for (String content : functionContent) {
            executeLine(content, true);// Run on the function's content and executing it
        }
    }

    // fun <fun_name> % <variables[,]> % $ <content[;]> $ 
    private static void executeFunction(String[] tokens) throws Exception {
        if (tokens.length < 3) {
            throw new Exception("Syntax Error: Expected '%' at position 2 in the token array, but the array is too short.");
        }
    
        String functionName = tokens[1];
        List<String> functionVariables = new ArrayList<>();
        List<String> functionContent = new ArrayList<>();
    
        if (!tokens[2].equals("%")) {
            throw new Exception("Syntax Error: Expected '%' at position 2 in the token array.");
        }
    
        int i = 3;
        while (i < tokens.length && !tokens[i].equals("%")) {
            String token = tokens[i];
    
            if (!findIfNumberOnly(token)) {
                throw new Exception("Mismatching Argument: variables cannot be only numbers");
            }
            if (!token.equals(","))
                functionVariables.add(token);
            i++;
        }
        if (i >= tokens.length || !tokens[i].equals("%")) {
            throw new Exception("Syntax Error: Expected '%' at position " + i + " in the token array.");
        }
        i++;
        if (i >= tokens.length || !tokens[i].equals("$")) {
            throw new Exception("Syntax Error: Expected '$' after token[" + i + "] in the token array.");
        }
        i++;
        StringBuilder contentBuilder = new StringBuilder();
        while (i < tokens.length && !tokens[i].equals("$")) {
            contentBuilder.append(tokens[i]).append(" ");
            i++;
        }
        if (i >= tokens.length || !tokens[i].equals("$")) {
            throw new Exception("Syntax Error: Expected closing '$' in the token array.");
        }
        String[] commands = contentBuilder.toString().split(";");
        for (String command : commands) {
            if (!command.trim().isEmpty()) {
                functionContent.add(command.trim());
            }
        }
        Map.Entry<List<String>, List<String>> entry = new AbstractMap.SimpleEntry<>(functionVariables, functionContent);
        functions.put(functionName, entry);
    }
    private static boolean findIfNumberOnly(String token) {
        return !token.matches("\\d+");
    }
      
    private static void executeVarDeclaration(String[] tokens) {
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

    private static void executePrint(String[] tokens) {
        double num;
        if (tokens.length < 2) {
            System.out.println("Invalid print statement: " + String.join(" ", tokens));
            return;
        }
        String varName = tokens[1];

        if (variables.containsKey(varName)) {
            int varValue = variables.get(varName);
            if (tokens.length > 2 && isMathExpression(tokens)) {
                double result = evaluateMathExpression(Arrays.copyOfRange(tokens, 2, tokens.length), varValue);
                System.out.println(result);
            } else {
                System.out.println(varValue);
            }
        } else {
            if (!isNum(tokens[1]))// TODO: getting variables into the function and executing on them stuff
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

    private static double strToDouble(String string) {
        return Double.parseDouble(string);
    }

    private static boolean isNum(String value) {
        try {
            Float.parseFloat(value);
            return true;

        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void executeIf(String[] tokens) {
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

    private static boolean isMathExpression(String[] tokens) {
        for (String token : tokens) {
            if (token.matches("[+\\-*/]")) {
                return true;
            }
        }
        return false;
    }

    private static double evaluateMathExpression(String[] expression, double initialValue) {
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
                            throw new Error("Exception: Division by zero!");
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

    public static void main(String[] args) throws Exception {
        Interpreter interpreter = new Interpreter();
        interpreter.execute();
    }
}
