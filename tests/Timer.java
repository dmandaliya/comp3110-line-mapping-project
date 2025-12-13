
import java.time.Duration;
import java.time.LocalDateTime;

public class Timer {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean running;

    public Timer() {
        this.running = false;
    }

    public void start() {
        if (running) {
            throw new IllegalStateException("Timer is already running");
        }
        startTime = LocalDateTime.now();
        running = true;
    }

    public void stop() {
        if (!running) {
            throw new IllegalStateException("Timer is not running");
        }
        endTime = LocalDateTime.now();
        running = false;
    }

    public long getElapsedSeconds() {
        if (startTime == null) {
            return 0;
        }
        LocalDateTime end = running ? LocalDateTime.now() : endTime;
        return Duration.between(startTime, end).getSeconds();
    }

    public void reset() {
        startTime = null;
        endTime = null;
        running = false;
    }
}
