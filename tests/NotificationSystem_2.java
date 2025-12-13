
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationSystem {

    private List<Notification> notifications;
    private int maxNotifications;

    public NotificationSystem() {
        this(100);
    }

    public NotificationSystem(int maxNotifications) {
        this.notifications = new ArrayList<>();
        this.maxNotifications = maxNotifications;
    }

    static class Notification {
        String message;
        String type;
        LocalDateTime timestamp;
        boolean read;

        Notification(String message, String type, LocalDateTime timestamp) {
            this.message = message;
            this.type = type;
            this.timestamp = timestamp;
            this.read = false;
        }

        void markAsRead() {
            this.read = true;
        }
    }

    public void addNotification(String message, String type) {
        Notification notification = new Notification(message, type, LocalDateTime.now());
        notifications.add(0, notification);
        if (notifications.size() > maxNotifications) {
            notifications.remove(notifications.size() - 1);
        }
    }

    public List<Notification> getAll() {
        return new ArrayList<>(notifications);
    }

    public List<Notification> getUnread() {
        return notifications.stream()
                .filter(n -> !n.read)
                .collect(Collectors.toList());
    }

    public void markAllAsRead() {
        for (Notification notification : notifications) {
            notification.markAsRead();
        }
    }

    public void clearAll() {
        notifications.clear();
    }

    public int getCount() {
        return notifications.size();
    }

    public int getUnreadCount() {
        return (int) notifications.stream().filter(n -> !n.read).count();
    }
}
