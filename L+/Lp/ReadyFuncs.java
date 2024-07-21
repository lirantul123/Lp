package Lp;
import java.util.HashMap;
import java.util.List;

public class ReadyFuncs {

    private HashMap<String, Integer> intVariables;
    private HashMap<String, String> stringVariables;

    public ReadyFuncs(HashMap<String, Integer> intVariables, HashMap<String, String> stringVariables) {
        this.intVariables = intVariables;
        this.stringVariables = stringVariables;
    }

    public int len(String varName) {
        if (!intVariables.containsKey(varName) && !stringVariables.containsKey(varName)) {
            throw new IllegalArgumentException("Variable not found: " + varName);
        }
        Object varValue;
        if (intVariables.containsKey(varName)) {
            varValue = intVariables.get(varName);
        } else {
            varValue = stringVariables.get(varName);
        }
    
        if (varValue instanceof String) {
            String[] words = ((String) varValue).trim().split("\\s+");
            return words.length;
        } else if (varValue instanceof List) {
            return ((List<?>) varValue).size();
        } else if (varValue.getClass().isArray()) {
            return java.lang.reflect.Array.getLength(varValue);
        } else if (varValue instanceof Integer || varValue instanceof Double) {
            System.out.println("This is a Num.\nIf you want the length, convert it to String, List or Array.");
            return -1;
        } else if (varValue instanceof Boolean) {
            System.out.println("This is a Boolean.\nIf you want the length, convert it to String, List or Array.");
            return -1;
        } else {
            throw new IllegalArgumentException("Unsupported type: " + varValue.getClass());
        }
    }
    
    protected static String toUpperC(String str) {
        String ans = "";

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ' ')
                ans += " ";
            else if ((int) (str.charAt(i)) >= 97 && (int) (str.charAt(i)) <= 122)
                ans += str.charAt(i);
            else {
                int asciPlace = (int) str.charAt(i) + 32;
                ans += (char) (asciPlace);
            }
        }
        return ans;
    }

    protected static String toLowerC(String str) {
        String ans = "";

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ' ')
                ans += " ";
            if ((int) (str.charAt(i)) >= 65 && (int) (str.charAt(i)) <= 90)
                ans += str.charAt(i);
            else {
                int asciPlace = (int) str.charAt(i) - 32;
                ans += (char) (asciPlace);
            }
        }
        return ans;
    }

    protected static String stringToInt(String str) {
        StringBuilder result = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
                result.append(c);
            } else if (Character.isLetter(c)) {
                result.append('(').append((int) c).append(')');
            }
        }
        return result.toString();
    }

    protected static String intToString(int num) {
        String str = String.valueOf(num);
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < str.length()) {
            if (str.charAt(i) == '(') {
                int endIndex = str.indexOf(')', i);
                if (endIndex != -1) {
                    int asciiValue = Integer.parseInt(str.substring(i + 1, endIndex));
                    result.append((char) asciiValue);
                    i = endIndex + 1;
                } else {
                    // Invalid input format
                    throw new IllegalArgumentException("Invalid input format: " + str);
                }
            } else {
                result.append(str.charAt(i));
                i++;
            }
        }
        return result.toString();
    }
}
