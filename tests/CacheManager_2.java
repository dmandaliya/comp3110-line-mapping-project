
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CacheManager {

    private Map<String, CacheEntry> cache;
    private int maxSize;
    private long defaultTtl;

    public CacheManager(int maxSize, long defaultTtl) {
        this.cache = new LinkedHashMap<>(maxSize, 0.75f, true);
        this.maxSize = maxSize;
        this.defaultTtl = defaultTtl;
    }

    static class CacheEntry {
        Object value;
        long expiryTime;

        CacheEntry(Object value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    public void put(String key, Object value) {
        put(key, value, defaultTtl);
    }

    public void put(String key, Object value, long ttl) {
        if (cache.size() >= maxSize && !cache.containsKey(key)) {
            String firstKey = cache.keySet().iterator().next();
            cache.remove(firstKey);
        }
        long expiryTime = System.currentTimeMillis() + ttl;
        cache.put(key, new CacheEntry(value, expiryTime));
    }

    public Object get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null) {
            return null;
        }
        if (entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        return entry.value;
    }

    public boolean contains(String key) {
        CacheEntry entry = cache.get(key);
        if (entry != null && entry.isExpired()) {
            cache.remove(key);
            return false;
        }
        return entry != null;
    }

    public void remove(String key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }

    public int size() {
        cleanExpired();
        return cache.size();
    }

    private void cleanExpired() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}
