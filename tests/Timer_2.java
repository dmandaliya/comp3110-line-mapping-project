
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Timer {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean running;
    private List<Long> laps;

    public Timer() {
        this.running = false;
        this.laps = new ArrayList<>();
    }

    public void start() {
        if (running) {
            throw new IllegalStateException("Timer is already running");
        }
        startTime = LocalDateTime.now();
        endTime = null;
        running = true;
        laps.clear();
    }

    public void stop() {
        if (!running) {
            throw new IllegalStateException("Timer is not running");
        }
        endTime = LocalDateTime.now();
        running = false;
    }

    public void lap() {
        if (!running) {
            throw new IllegalStateException("Timer is not running");
        }
        laps.add(getElapsedMillis());
    }

    public List<Long> getLaps() {
        return new ArrayList<>(laps);
    }

    public long getElapsedSeconds() {
        if (startTime == null) {
            return 0;
        }
        LocalDateTime end = running ? LocalDateTime.now() : endTime;
        return Duration.between(startTime, end).getSeconds();
    }

    public long getElapsedMillis() {
        if (startTime == null) {
            return 0;
        }
        LocalDateTime end = running ? LocalDateTime.now() : endTime;
        return Duration.between(startTime, end).toMillis();
    }

    public boolean isRunning() {
        return running;
    }

    public void reset() {
        startTime = null;
        endTime = null;
        running = false;
        laps.clear();
    }

    public String format() {
        long seconds = getElapsedSeconds();
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
}
