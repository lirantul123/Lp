public class readyFuncs {
    
    public static String toUpperC(String str){
        String ans = "";

        for (int i = 0; i < str.length(); i++) {

            if (str.charAt(i) == ' ')
                ans += " ";
            else if ((int)(str.charAt(i)) >= 97 && (int)(str.charAt(i)) <= 122)
                ans += str.charAt(i);
            else {
                int asciPlace = (int)str.charAt(i) + 32;
                ans += (char)(asciPlace);
            }
        }
        return ans;
    }

    public static String toLowerC(String str){
        String ans = "";

        for (int i = 0; i < str.length(); i++) {

            if (str.charAt(i) == ' ')
                ans += " ";
            if ((int)(str.charAt(i)) >= 65 && (int)(str.charAt(i)) <= 90)
                ans += str.charAt(i);
            else {
                int asciPlace = (int)str.charAt(i) - 32;
                ans += (char)(asciPlace);
            }
        }
        return ans;
    }
    
    public static String stringToInt(String str) {
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

    public static String intToString(int num) {
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

    public static void main(String[] args) {
        // String text = "HeLlO wORlD";

        // System.out.println(toUpperC(text));
        // System.out.println(toLowerC(text));
    }
}
