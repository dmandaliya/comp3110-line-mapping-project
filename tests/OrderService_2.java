package mytool.dataset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderService {

    private final List<String> orders = new ArrayList<>();

    public void addOrder(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id");
        }
        if (orders.contains(id)) {
            return;
        }
        orders.add(id);
    }

    public boolean hasOrder(String id) {
        if (id == null) {
            return false;
        }
        return orders.contains(id);
    }

    public int getOrderCount() {
        return orders.size();
    }

    public List<String> getOrderSnapshot() {
        return Collections.unmodifiableList(new ArrayList<>(orders));
    }

    public void cancelAll() {
        orders.clear();
    }
}
