public class toCase {
    
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
    

    public static void main(String[] args) {
        String text = "HeLlO wORlD";

        System.out.println(toUpperC(text));
        System.out.println(toLowerC(text));

    }
}
