
import java.util.regex.Pattern;

public class EmailValidator {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public boolean isValid(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return false;
        }

        String[] parts = email.split("@");
        String local = parts[0];
        String domain = parts[1];

        if (local.length() > 64 || domain.length() > 255) {
            return false;
        }

        return true;
    }

    public String getDomain(String email) {
        if (!isValid(email)) {
            return null;
        }
        return email.split("@")[1];
    }

    public String getLocalPart(String email) {
        if (!isValid(email)) {
            return null;
        }
        return email.split("@")[0];
    }
}
