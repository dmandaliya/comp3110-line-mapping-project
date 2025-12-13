
import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;

public class UserProfile {

    private final String username;
    private final String email;
    private LocalDate createdAt;
    private boolean active;
    private Locale preferredLocale;

    public UserProfile(String username, String email) {
        this.username = Objects.requireNonNull(username, "username");
        this.email = normalizeEmail(email);
        this.createdAt = LocalDate.now();
        this.active = true;
        this.preferredLocale = Locale.getDefault();
    }

    private String normalizeEmail(String email) {
        Objects.requireNonNull(email, "email");
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false;
    }

    public void reactivate() {
        this.active = true;
    }

    public Locale getPreferredLocale() {
        return preferredLocale;
    }

    public void setPreferredLocale(Locale preferredLocale) {
        this.preferredLocale = Objects.requireNonNull(preferredLocale, "preferredLocale");
    }
}
