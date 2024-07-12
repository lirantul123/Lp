//import java.util.*; all bellow ↓
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// TODO: modify the enum
public class Interpreter {

    public static final int incredecreValue = 1;
    public enum FAMWORDS {
        var,
        perSign,
        dollSign,
        pp,// ++
        mm,// --
        ifWord,
        whileWord,
        forWord,
        funWord,
        printWord,
        clearWord,
        exitWord
    }    
    private static HashMap<String, Integer> variables;
    // [fun_name], [variables, content]
    private static HashMap<String, Map.Entry<List<String>, List<String>>> functions;

    public Interpreter() {
        Interpreter.variables = new HashMap<>();
        Interpreter.functions = new HashMap<>();
    }

    public void execute() {
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
                if (line.trim().toLowerCase().equals("clear") || line.trim().toLowerCase().equals("cls")) {
                    cleanScreen();
                    cls = true;
                }
                if (cls)
                    continue;

                runCode(line);
                System.out.print(">> ");
            }
        }
    }

    private void cleanScreen() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }
    private void runCode(String code) {
        String[] lines = code.split("\n");
        for (String line : lines) {
            executeLine(line.trim(), false);
        }
    }

    private static void executeLine(String line, boolean innerFun)  {
        if (line.isEmpty()) {
            return;// empty lines is okay 𓆝 𓆟 𓆞 𓆝 𓆟 
        }
        String[] tokens = line.split("\\s+");
        switch (tokens[0]) {
            case "var":// variable
            case "VAR":
                executeVarDeclaration(tokens);
                break;
            case "print":// print
            case "PRINT":
                executePrint(tokens);
                break;
            case "if":// if
            case "IF":
                executeIf(Arrays.copyOfRange(tokens, 1, tokens.length));
                break;
            case "fun":// function
            case "FUN":
                if (!innerFun)
                    executeFunction(tokens);
                else
                System.out.println("\n| 'Syntax Error: Function definition cannot be inside one.' |\n");
            // case "while":// while - SHIT FOR NOW
            // case "WHILE":
            //     executeWhile(tokens);
            //     break;
            // case "for":// for - SHIT FOR NOW
            // case "For":
            //     executeFor(tokens);
            //     break;
            case "/":// comment
                break;
            default:
                // <fun_name> % <variables[,]> %
                if (!tokens[1].equals("++") && !tokens[1].equals("--"))
                    executeExistFunction(tokens);
                else{
                    if (tokens[1].equals("++")){
                        incrementVar(tokens);// increment by 1
                    }
                    else
                        decrementVar(tokens);// decrement by 1
                }
        }
    }

    private static void incrementVar(String[] tokens) {
        if (tokens.length < 2) {
            System.out.println("Invalid decrement statement: " + String.join(" ", tokens));
            return;
        }
        String varName = tokens[0];

        if (variables.containsKey(varName)) {
            int currentValue = variables.get(varName);
            variables.put(varName, currentValue + incredecreValue);
        } else {
            System.out.println("Variable not found: " + varName);
        }

    }

    private static void decrementVar(String[] tokens) {
        if (tokens.length < 2) {
            System.out.println("Invalid decrement statement: " + String.join(" ", tokens));
            return;
        }
        String varName = tokens[0];

        if (variables.containsKey(varName)) {
            int currentValue = variables.get(varName);
            variables.put(varName, currentValue - incredecreValue);
        } else {
            System.out.println("Variable not found: " + varName);
        }
    }


    // var a = 1; while % a < 2 % $ print a; a ++; print a; $
    private static void executeWhile(String[] tokens)  {
        if (tokens.length < 5 || !tokens[2].equals("%") || !tokens[tokens.length - 1].equals("$")) {
            System.out.println("\n| 'Syntax Error: Expected '%' and '$' for while loop.' |\n");
        }
    
        String condition = tokens[3];
        String[] conditionParts = condition.split("\\s+");
        
        if (conditionParts.length != 3) {
            System.out.println("\n| 'Syntax Error: Invalid condition in while loop.' |\n");
        }
    
        StringBuilder bodyBuilder = new StringBuilder();
        for (int i = 4; i < tokens.length - 1; i++) {
            bodyBuilder.append(tokens[i]).append(" ");
        }
        String[] bodyLines = bodyBuilder.toString().split(";");
    
        while (evaluateCondition(conditionParts[0], conditionParts[1], conditionParts[2])) {
            for (String line : bodyLines) {
                executeLine(line.trim(), false);
            }
        }
    }
    
    // for Conditions
    private static boolean evaluateCondition(String leftOperand, String operator, String rightOperand) {
        int leftValue = getValue(leftOperand);
        int rightValue = getValue(rightOperand);

        switch (operator) {
            case "<":
                return leftValue < rightValue;
            case ">":
                return leftValue > rightValue;
            case "==":
                return leftValue == rightValue;
            case "!=":
                return leftValue != rightValue;
            case "<=":
                return leftValue <= rightValue;
            case ">=":
                return leftValue >= rightValue;
            default:
                System.out.println("\n| 'Invalid operator in condition: " + operator + "' |\n");
                return false;
        }
    }
    private static int getValue(String operand) {
        if (variables.containsKey(operand)) {
            return variables.get(operand);
        }
        try {
            return Integer.parseInt(operand);
        } catch (NumberFormatException e) {
            System.out.println("\n| 'Invalid operand: " + operand + "' |\n");
        }
        return -1;
    }

    private static void executeFor(String[] tokens) {
        if (tokens.length < 7 || !tokens[1].equals("%") || !tokens[tokens.length - 1].equals("$")) {
            System.out.println("\n| 'Syntax Error: Expected '%' and '$' for for loop.' |\n");
        }

        String varName = tokens[2];
        int start = Integer.parseInt(tokens[3]);
        int end = Integer.parseInt(tokens[5]);
        StringBuilder bodyBuilder = new StringBuilder();
        for (int i = 7; i < tokens.length - 1; i++) {
            bodyBuilder.append(tokens[i]).append(" ");
        }
        String[] bodyLines = bodyBuilder.toString().split(";");

        for (int i = start; i < end; i++) {
            variables.put(varName, i);
            for (String line : bodyLines) {
                executeLine(line.trim(), false);
            }
        }
    }

    private static void executeExistFunction(String[] tokens) {
        String functionName = tokens[0];
        if (functions.containsKey(functionName)) {
            executeUserFunction(functionName, tokens);
        } else {
            System.out.println("Invalid statement or function not found: " + functionName);
        }
    }

    private static void executeUserFunction(String functionName, String[] tokens) {
        boolean cannot_run_function = false;

        if (tokens.length < 3 || !tokens[1].equals("%") || !tokens[tokens.length - 1].equals("%")) {
            System.out.println("\n| 'Syntax Error: Expected '%' to surround the function variables.' |\n");
        }

        String[] variableTokens = String.join(" ", Arrays.copyOfRange(tokens, 2, tokens.length - 1)).split("\\s*,\\s*");
        List<String> providedVariables = Arrays.asList(variableTokens);

        for (int i = 0; i < providedVariables.size(); i++) {// only if the variables were setted to value
            String var = providedVariables.get(i);
            if (!variables.containsKey(var)){
                System.out.println("\n| Variable - '" + var + "' was not setted. Therfore, cannot execute - '" + functionName + "' method |\n");
                cannot_run_function = true;
            }
        }
        
        Map.Entry<List<String>, List<String>> function = functions.get(functionName);
        List<String> functionVariables = function.getKey();
        List<String> functionContent = function.getValue();

        if (providedVariables.size() != functionVariables.size()) {
            System.out.println("\n| 'Mismatching Argument: The number of provided variables does not match the function's variables.' |\n");
        }

        // if (cannot_run_function){
        //     System.out.println("\n| Cannot execute - '" + functionName + "' method ↑ |\n");
        // }
        // else {
        //     System.out.println("Executing function " + functionName + " with variables: " + providedVariables);
        //     for (String content : functionContent) {
        //         executeLine(content, true);
        //     }
        // } ↓ same ↓
        System.out.println(cannot_run_function ? "\n| Cannot execute - '" + functionName +
                                                 "' method ↑ |\n" : executeFunContent(functionName, providedVariables, functionContent));
    }

    private static String executeFunContent(String functionName, List<String> providedVariables, List<String> functionContent) {
        String mess = "Executing function " + functionName + " with variables: " + providedVariables;
        for (String content : functionContent) {
            executeLine(content, true);
        }
        return mess;
    }

    private static void executeFunction(String[] tokens) {
        if (tokens.length < 3) {
            System.out.println("\n| 'Syntax Error: Expected '%' at position 2 in the token array, but the array is too short.' |\n");
        }

        String functionName = tokens[1];
        List<String> functionVariables = new ArrayList<>();
        List<String> functionContent = new ArrayList<>();

        if (!tokens[2].equals("%")) {
            System.out.println("\n| 'Syntax Error: Expected '%' at position 2 in the token array.' |\n");
        }

        int i = 3;
        while (i < tokens.length && !tokens[i].equals("%")) {
            String token = tokens[i];

            if (!findIfNumberOnly(token)) {
                System.out.println("\n| 'Mismatching Argument: variables cannot be only numbers' |\n");
            }
            if (!token.equals(","))
                functionVariables.add(token);
            i++;
        }
        if (i >= tokens.length || !tokens[i].equals("%")) {
            System.out.println("\n| 'Syntax Error: Expected '%' at position " + i + " in the token array.' |\n");
        }
        i++;
        if (i >= tokens.length || !tokens[i].equals("$")) {
            System.out.println("\n| 'Syntax Error: Expected '$' after token[" + i + "] in the token array.' |\n");
        }
        i++;
        StringBuilder contentBuilder = new StringBuilder();
        while (i < tokens.length && !tokens[i].equals("$")) {
            contentBuilder.append(tokens[i]).append(" ");
            i++;
        }
        if (i >= tokens.length || !tokens[i].equals("$")) {
            System.out.println("\n\n| 'Syntax Error: Expected closing '$' in the token array.' |\n");
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
                            System.out.println("\n| 'Exception: Division by zero!' |");
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
        try {
            Interpreter interpreter = new Interpreter();
            interpreter.execute();

        } catch (Exception e) {
            System.out.println("Error in initializing the interpreter: " + e.getMessage());
        }
    }
}