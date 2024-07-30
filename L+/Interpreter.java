import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// TODO: Correct while loop and for loop
// TODO: if function...

public class Interpreter {

    public static final int IncreDecreValue = 1;
    // Familiar words with the system
    public enum FAMWORDS {
        VAR("var"), PRINT("print"), IF("if"), WHILE("while"),
        FOR("for"), FUN("fun"), CLEAR("clear"), EXIT("exit"),
        COMMENT("/"), LEN("len"),
        PP("++"), MM("--");

        private final String keyword;

        FAMWORDS(String keyword) {
            this.keyword = keyword;
        }
        public String getKeyword() {
            return keyword;
        }
        public static FAMWORDS fromString(String keyword) {// searching for the value (var√ print√... etc)
            for (FAMWORDS word : FAMWORDS.values()) {
                if (word.keyword.equalsIgnoreCase(keyword)) {
                    return word;
                }
            }
            return null;
        }
    }
       
    private static HashMap<String, Double> intVariables;
    private static HashMap<String, String> stringVariables;
    // [fun_name], [variables, content]
    private static HashMap<String, Map.Entry<List<String>, List<String>>> functions;

    public Interpreter() {
        Interpreter.intVariables = new HashMap<>();
        Interpreter.stringVariables = new HashMap<>();
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
                if (line.trim().equalsIgnoreCase(FAMWORDS.EXIT.getKeyword())) {// leave the interpreter 
                    break;
                }
                if (line.trim().equalsIgnoreCase(FAMWORDS.CLEAR.getKeyword())) {// clear the screen
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
            return;// Empty lines are accaptable and ignored 𓆝 𓆟 𓆞 𓆝 𓆟 
        }

        String[] tokens = line.split("\\s+");
        FAMWORDS keyword = FAMWORDS.fromString(tokens[0]);

        if (keyword == null) {
            // Handle the case where the keyword is null
            if (!revaluateVariable(tokens)) {
                if (!tokens[1].trim().equalsIgnoreCase(FAMWORDS.PP.getKeyword()) && !tokens[1].trim().equalsIgnoreCase(FAMWORDS.MM.getKeyword())) {
                    // <fun_name> % <variables[,]> %
                    executeExistFunction(tokens);
                } else {
                    if (tokens[1].trim().equalsIgnoreCase(FAMWORDS.MM.getKeyword())) {
                        decrementVar(tokens); // decrement by 1
                    } else {
                        incrementVar(tokens); // increment by 1
                    }
                }
            } else {
                System.out.println("Mission Accomplished");
            }
            return;
        }
    
        switch (keyword) {
            case VAR: // variable
                executeVarDeclaration(tokens);
                break;
            case PRINT: // print
                executePrint(tokens);
                break;
            case IF: // if
                executeIf(Arrays.copyOfRange(tokens, 1, tokens.length));
                break;
            case FUN: // function
                if (!innerFun) {
                    executeFunction(tokens);
                } else {
                    System.out.println("\n| 'Syntax Error: Function definition cannot be inside one.' |\n");
                }
                break;
            case WHILE: // while - SHIT FOR NOW
                executeWhile(tokens);
                break;
            case FOR: // for - SHIT FOR NOW
                executeFor(tokens);
                break;
            case COMMENT: // comment
                break;
            case LEN:// length of Words/Letters in String, List or Array
                //ReadyFuncs r = new ReadyFuncs(intVariables, stringVariables);
                //System.out.println("Variable '" + tokens[1] + "' length's is _" + r.len(tokens[1]) + "_");
                len(tokens, 'l');
                System.out.println(" letters");
                len(tokens, 'w');
                System.out.println(" words");
                break;
            default:
                System.out.println("Unexpected keyword: " + tokens[0]);
        }
    }

    private static boolean revaluateVariable(String[] tokens) {
        if (tokens.length < 3) {
            System.out.println("Invalid Revaluate Variable statement: " + String.join(" ", tokens));
            return false;
        }
        // TODO: Handle erroring in type
        String varName = tokens[0];
        if (intVariables.containsKey(varName) ) {
            double currentValue = Double.parseDouble(tokens[2]);
            intVariables.put(varName, currentValue);
            return true;
        }else if (stringVariables.containsKey(varName)) {
            stringVariables.put(varName, tokens[2]);
            return true;
        } else {
            System.out.println("Variable not found: " + varName);
            return false;
        }
    }

    private static void incrementVar(String[] tokens) {
        if (tokens.length < 2) {
            System.out.println("Invalid decrement statement: " + String.join(" ", tokens));
            return;
        }
        String varName = tokens[0];

        if (intVariables.containsKey(varName)) {
            double currentValue = intVariables.get(varName);
            intVariables.put(varName, currentValue + IncreDecreValue);
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

        if (intVariables.containsKey(varName)) {
            double currentValue = intVariables.get(varName);
            intVariables.put(varName, currentValue - IncreDecreValue);
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
        double leftValue = getValue(leftOperand);
        double rightValue = getValue(rightOperand);

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
    private static double getValue(String operand) {
        if (intVariables.containsKey(operand)) {// here String also shoud be a prospect
            return intVariables.get(operand);
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
            intVariables.put(varName, (double)(i));
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

        boolean skipBeNum = true;
        for (int i = 0; i < providedVariables.size(); i++) {// only if the variables were setted to value

            String var = providedVariables.get(i);

            for (int j = 0; j < var.length(); j++) {
                if (!((int)(var.charAt(i)) >= 97 && (int)(var.charAt(i)) <= 122) || !((int)(var.charAt(i)) >= 65 && (int)(var.charAt(i)) <= 90))// only number...
                    continue;
                else{
                    skipBeNum = false;
                }
            }
            if (!skipBeNum){
                if (!intVariables.containsKey(var)){
                    System.out.println("\n| Variable - '" + var + "' was not setted. Therfore, cannot execute - '" + functionName + "' method |\n");
                    cannot_run_function = true;
                }
            }
        }
        
        Map.Entry<List<String>, List<String>> function = functions.get(functionName);
        List<String> functionVariables = function.getKey();
        List<String> functionContent = function.getValue();

        if (providedVariables.size() != functionVariables.size()) {
            System.out.println("\n| 'Mismatching Argument: The number of provided variables does not match the function's variables.' |\n");
        }

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
        if (intVariables.containsKey(varName) || stringVariables.containsKey(varName)) {
            System.out.println("Variable already exist! Sorry but you cannot redeclare.");
            return;
        }

        String varStringValue = "";
        if (tokens[3].equals("'"))// string/char
        {
            if (!tokens[tokens.length - 1].equals("'"))
                System.out.println("\n| 'Syntax Error: Expected ' in the end of the value. |\n");
            else{
                for (int i = 4; i < tokens.length; i++) {
                    if (tokens[i].equals("'"))
                        break;
                    varStringValue += tokens[i] + " "; 
                }
                stringVariables.put(varName, varStringValue);
            }
        }else{// int/double
            try {
                double varValue = Double.parseDouble(tokens[3]);
                intVariables.put(varName, varValue);
            } catch (NumberFormatException e) {
                System.out.println("Invalid variable value: " + tokens[3]);
            }
        }
    }

    private static void executePrint(String[] tokens) {
        double num;
        if (tokens.length < 2) {
            System.out.println("Invalid print statement: " + String.join(" ", tokens));
            return;
        }
        String varName = tokens[1];

        if (intVariables.containsKey(varName)) {
            double varValue = intVariables.get(varName);
            if (tokens.length > 2 && isMathExpression(tokens)) {
                double result = evaluateMathExpression(Arrays.copyOfRange(tokens, 2, tokens.length), varValue);
                System.out.println(result);
            } else {
                System.out.println(varValue);
            }
        } else if(stringVariables.containsKey(varName)){
            String varValue = stringVariables.get(varName);
            System.out.println(varValue);
        }else {
            if (!isNum(tokens[1]))// Should be changes, because string is a prospect
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

        // Accaptable: if x + 3 - 1 > 5 * a // $ $
        // String varName = tokens[1];
        // double num;
        // if (intVariables.containsKey(varName)) {
        //     int varValue = intVariables.get(varName);
        //     if (tokens.length > 2 && isMathExpression(tokens)) {
        //         double result = evaluateMathExpression(Arrays.copyOfRange(tokens, 2, tokens.length), varValue);
        //         System.out.println(result);
        //     } else {
        //         System.out.println(varValue);
        //     }
        // } else if(stringVariables.containsKey(varName)){
        //     String varValue = stringVariables.get(varName);
        //     System.out.println(varValue);
        // }else {
        //     if (!isNum(tokens[1]))// Should be changes, because string is a prospect
        //         System.out.println("smt- we support only numbers for now: " + varName);
        //     else{
        //         if (tokens.length > 2 && isMathExpression(tokens)) {
        //             num = strToDouble(tokens[1]);
        //             double result = evaluateMathExpression(Arrays.copyOfRange(tokens, 2, tokens.length), num);
        //             System.out.println(result);
        //         }
        //         else
        //             System.out.println(tokens[1]);
        //     }
        // }

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
                if (intVariables.containsKey(token)) {
                    operand = intVariables.get(token);
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

    public static int len(String[] tokens, char c) {
        String varValue = "";
        if (tokens[1].equals("'")){
            for (int i = 2; i < tokens.length; i++) {
                if (tokens[i].equals("'"))
                    break;
                varValue += tokens[i];
                varValue += " ";
            }
            System.out.println(returnLength(varValue, c, false));
        }
        else{
            String varName = tokens[1];
            if (!intVariables.containsKey(varName) && !stringVariables.containsKey(varName)) {
                System.out.println("Variable not found: " + varName);
                return 0;
            }
            System.out.println(returnLength(varName, c, true));
        }
        return 1;
    }
    public static int returnLength(Object varNameValue, char c, boolean isVariable){
        if (isVariable){
            Object varValue;
            if (intVariables.containsKey(varNameValue)) {
                varValue = intVariables.get(varNameValue);
            } else {
                varValue = stringVariables.get(varNameValue);
            }
        
            if (varValue instanceof String) {
                String[] words = ((String) varValue).trim().split("\\s+");
                String[] letters = ((String) varValue).trim().split("");
                return (c == 'w') ? words.length : letters.length;
            } 
            // else if (varValue instanceof List) {
            //     return ((List<?>) varValue).size();
            // } else if (varValue.getClass().isArray()) {
            //     return java.lang.reflect.Array.getLength(varValue);
            // } 
            else if (varValue instanceof Integer || varValue instanceof Double) {
                System.out.println("This is a Num.\nIf you want the length, convert it to String, List or Array.");
                return -1;
            } else if (varValue instanceof Boolean) {
                System.out.println("This is a Boolean.\nIf you want the length, convert it to String, List or Array.");
                return -1;
            } else {
                throw new IllegalArgumentException("Unsupported type: " + varValue.getClass());
            }    
        }
        else{
            if (varNameValue instanceof String) {
                String[] words = ((String) varNameValue).trim().split("\\s+");
                String[] letters = ((String) varNameValue).trim().split("");
                return (c == 'w') ? words.length : letters.length;
            } 
            // else if (varValue instanceof List) {
            //     return ((List<?>) varValue).size();
            // } else if (varValue.getClass().isArray()) {
            //     return java.lang.reflect.Array.getLength(varValue);
            // } 
            else if (varNameValue instanceof Integer || varNameValue instanceof Double) {
                System.out.println("This is a Num.\nIf you want the length, convert it to String, List or Array.");
                return -1;
            } else if (varNameValue instanceof Boolean) {
                System.out.println("This is a Boolean.\nIf you want the length, convert it to String, List or Array.");
                return -1;
            } else {
                throw new IllegalArgumentException("Unsupported type: " + varNameValue.getClass());
            }    

        }
    }
    
    public static void main(String[] args) {
        try {
            Interpreter interpreter = new Interpreter();
            interpreter.execute();

        } catch (Exception e) {
            System.out.println("Error in initializing the interpreter:\n " + e.getMessage() + "\n");
        } 
    }
}