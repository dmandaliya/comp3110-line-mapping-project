class TextUtilsNew {

    public static boolean isPalindrome(String s) {
        return s.equals(reverse(s));
    }

    public static String reverse(String str) {
        StringBuilder sb = new StringBuilder();
        for (int j = str.length() - 1; j >= 0; j--) {
            sb.append(str.charAt(j));
        }
        return sb.toString();
    }

    public static int countVowels(String input) {
        int count = 0;
        input = input.toLowerCase();
        for (int k = 0; k < input.length(); k++) {
            char ch = input.charAt(k);
            if (ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u') {
                count++;
            }
        }
        return count;
    }

}
