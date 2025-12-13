
import java.security.SecureRandom;

public class PasswordGenerator {

    private SecureRandom random;
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    public PasswordGenerator() {
        this.random = new SecureRandom();
    }

    public String generate(int length) {
        if (length < 4) {
            throw new IllegalArgumentException("Password length must be at least 4");
        }
        String chars = UPPERCASE + LOWERCASE + DIGITS;
        return generateFromCharset(chars, length);
    }

    public String generateWithSpecialChars(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password with special chars must be at least 8");
        }
        String chars = UPPERCASE + LOWERCASE + DIGITS + SPECIAL;
        StringBuilder password = new StringBuilder();
        
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));
        
        for (int i = 4; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return shuffleString(password.toString());
    }

    private String generateFromCharset(String charset, int length) {
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            password.append(charset.charAt(random.nextInt(charset.length())));
        }
        return password.toString();
    }

    private String shuffleString(String str) {
        char[] chars = str.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }

    public boolean isStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (UPPERCASE.indexOf(c) >= 0) hasUpper = true;
            else if (LOWERCASE.indexOf(c) >= 0) hasLower = true;
            else if (DIGITS.indexOf(c) >= 0) hasDigit = true;
            else if (SPECIAL.indexOf(c) >= 0) hasSpecial = true;
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}
