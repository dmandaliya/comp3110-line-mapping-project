
import java.util.ArrayList;
import java.util.List;

public class RateLimiter {

    private int maxRequests;
    private long windowMs;
    private List<Long> timestamps;

    public RateLimiter(int maxRequests, long windowMs) {
        this.maxRequests = maxRequests;
        this.windowMs = windowMs;
        this.timestamps = new ArrayList<>();
    }

    public boolean allowRequest() {
        long now = System.currentTimeMillis();
        cleanOldTimestamps(now);
        
        if (timestamps.size() < maxRequests) {
            timestamps.add(now);
            return true;
        }
        return false;
    }

    private void cleanOldTimestamps(long now) {
        timestamps.removeIf(timestamp -> now - timestamp > windowMs);
    }

    public int getRemainingRequests() {
        cleanOldTimestamps(System.currentTimeMillis());
        return Math.max(0, maxRequests - timestamps.size());
    }
}
