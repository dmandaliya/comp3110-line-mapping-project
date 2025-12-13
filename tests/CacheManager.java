
import java.util.HashMap;
import java.util.Map;

public class CacheManager {

    private Map<String, Object> cache;
    private int maxSize;

    public CacheManager(int maxSize) {
        this.cache = new HashMap<>();
        this.maxSize = maxSize;
    }

    public void put(String key, Object value) {
        if (cache.size() >= maxSize && !cache.containsKey(key)) {
            return;
        }
        cache.put(key, value);
    }

    public Object get(String key) {
        return cache.get(key);
    }

    public boolean contains(String key) {
        return cache.containsKey(key);
    }

    public void remove(String key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }

    public int size() {
        return cache.size();
    }
}
