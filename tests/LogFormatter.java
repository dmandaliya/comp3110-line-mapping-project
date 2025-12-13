
public class LogFormatter {

    public String formatInfo(String message) {
        return "[INFO] " + message;
    }

    public String formatWarning(String message) {
        return "[WARN] " + message;
    }

    public String formatError(String message) {
        return "[ERROR] " + message;
    }
}
