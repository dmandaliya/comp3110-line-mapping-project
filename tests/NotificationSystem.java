
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationSystem {

    private List<Notification> notifications;

    public NotificationSystem() {
        this.notifications = new ArrayList<>();
    }

    public void addNotification(String message, String type) {
        Notification notification = new Notification(message, type, LocalDateTime.now());
        notifications.add(notification);
    }

    public List<Notification> getAll() {
        return new ArrayList<>(notifications);
    }

    public void clearAll() {
        notifications.clear();
    }

    public int getCount() {
        return notifications.size();
    }

    static class Notification {
        String message;
        String type;
        LocalDateTime timestamp;

        Notification(String message, String type, LocalDateTime timestamp) {
            this.message = message;
            this.type = type;
            this.timestamp = timestamp;
        }
    }
}
