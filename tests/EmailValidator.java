
public class EmailValidator {

    public boolean isValid(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        if (!email.contains("@")) {
            return false;
        }

        String[] parts = email.split("@");
        if (parts.length != 2) {
            return false;
        }

        String local = parts[0];
        String domain = parts[1];

        if (local.isEmpty() || domain.isEmpty()) {
            return false;
        }

        if (!domain.contains(".")) {
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
}
