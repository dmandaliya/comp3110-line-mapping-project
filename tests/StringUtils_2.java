
public class StringUtils {

    private static final String VOWELS = "aeiouAEIOU";

    public static String reverse(String str) {
        if (str == null) {
            return null;
        }
        return new StringBuilder(str).reverse().toString();
    }

    public static boolean isPalindrome(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        String cleaned = str.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        if (cleaned.isEmpty()) {
            return false;
        }
        return cleaned.equals(reverse(cleaned));
    }

    public static int countVowels(String str) {
        if (str == null) {
            return 0;
        }
        int count = 0;
        for (char c : str.toCharArray()) {
            if (VOWELS.indexOf(c) != -1) {
                count++;
            }
        }
        return count;
    }

    public static int countConsonants(String str) {
        if (str == null) {
            return 0;
        }
        int count = 0;
        for (char c : str.toCharArray()) {
            if (Character.isLetter(c) && VOWELS.indexOf(c) == -1) {
                count++;
            }
        }
        return count;
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
