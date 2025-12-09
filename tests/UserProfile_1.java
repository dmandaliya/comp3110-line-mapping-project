package mytool.dataset;

import java.time.LocalDate;
import java.util.Objects;

public class UserProfile {

    private final String username;
    private final String email;
    private LocalDate createdAt;
    private boolean active;

    public UserProfile(String username, String email) {
        this.username = Objects.requireNonNull(username, "username");
        this.email = Objects.requireNonNull(email, "email");
        this.createdAt = LocalDate.now();
        this.active = true;
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
}
