
import java.time.Instant;

public class LogFormatter {

    public String formatInfo(String message) {
        return prefix("INFO", message);
    }

    public String formatWarning(String message) {
        return prefix("WARN", message);
    }

    public String formatError(String message) {
        return prefix("ERROR", message);
    }

    private String prefix(String level, String message) {
        String ts = Instant.now().toString();
        return "[" + level + "][" + ts + "] " + message;
    }
}
