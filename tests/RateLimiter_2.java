
import java.util.LinkedList;
import java.util.Queue;

public class RateLimiter {

    private int maxRequests;
    private long windowMs;
    private Queue<Long> timestamps;
    private long blockedUntil;

    public RateLimiter(int maxRequests, long windowMs) {
        this.maxRequests = maxRequests;
        this.windowMs = windowMs;
        this.timestamps = new LinkedList<>();
        this.blockedUntil = 0;
    }

    public boolean allowRequest() {
        long now = System.currentTimeMillis();
        
        if (now < blockedUntil) {
            return false;
        }
        
        cleanOldTimestamps(now);
        
        if (timestamps.size() < maxRequests) {
            timestamps.offer(now);
            return true;
        }
        
        blockedUntil = now + 1000;
        return false;
    }

    private void cleanOldTimestamps(long now) {
        while (!timestamps.isEmpty() && now - timestamps.peek() > windowMs) {
            timestamps.poll();
        }
    }

    public int getRemainingRequests() {
        long now = System.currentTimeMillis();
        if (now < blockedUntil) {
            return 0;
        }
        cleanOldTimestamps(now);
        return Math.max(0, maxRequests - timestamps.size());
    }

    public long getTimeUntilNextRequest() {
        long now = System.currentTimeMillis();
        if (now < blockedUntil) {
            return blockedUntil - now;
        }
        if (timestamps.size() < maxRequests) {
            return 0;
        }
        return windowMs - (now - timestamps.peek());
    }

    public void reset() {
        timestamps.clear();
        blockedUntil = 0;
    }
}
