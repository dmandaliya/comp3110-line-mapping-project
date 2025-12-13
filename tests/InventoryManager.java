
import java.util.ArrayList;
import java.util.List;

public class InventoryManager {

    private List<Item> inventory;

    public InventoryManager() {
        this.inventory = new ArrayList<>();
    }

    public void addItem(String name, int quantity, double price) {
        inventory.add(new Item(name, quantity, price));
    }

    public void removeItem(String name) {
        inventory.removeIf(item -> item.name.equals(name));
    }

    public void updateQuantity(String name, int quantity) {
        for (Item item : inventory) {
            if (item.name.equals(name)) {
                item.quantity = quantity;
                return;
            }
        }
    }

    public int getQuantity(String name) {
        for (Item item : inventory) {
            if (item.name.equals(name)) {
                return item.quantity;
            }
        }
        return 0;
    }

    public double getTotalValue() {
        double total = 0.0;
        for (Item item : inventory) {
            total += item.quantity * item.price;
        }
        return total;
    }

    static class Item {
        String name;
        int quantity;
        double price;

        Item(String name, int quantity, double price) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }
    }
}
