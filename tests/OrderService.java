
import java.util.ArrayList;
import java.util.List;

public class OrderService {

    private final List<String> orders = new ArrayList<>();

    public void addOrder(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id");
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

    public void cancelAll() {
        orders.clear();
    }
}
