
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InventoryManager {

    private List<Item> inventory;
    private double taxRate;

    public InventoryManager() {
        this(0.0);
    }

    public InventoryManager(double taxRate) {
        this.inventory = new ArrayList<>();
        this.taxRate = taxRate;
    }

    static class Item {
        String name;
        int quantity;
        double price;
        String category;

        Item(String name, int quantity, double price, String category) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
            this.category = category;
        }

        double getValue() {
            return quantity * price;
        }
    }

    public void addItem(String name, int quantity, double price, String category) {
        Optional<Item> existing = findItem(name);
        if (existing.isPresent()) {
            existing.get().quantity += quantity;
        } else {
            inventory.add(new Item(name, quantity, price, category));
        }
    }

    private Optional<Item> findItem(String name) {
        return inventory.stream()
                .filter(item -> item.name.equals(name))
                .findFirst();
    }

    public void removeItem(String name) {
        inventory.removeIf(item -> item.name.equals(name));
    }

    public void updateQuantity(String name, int quantity) {
        findItem(name).ifPresent(item -> item.quantity = quantity);
    }

    public int getQuantity(String name) {
        return findItem(name).map(item -> item.quantity).orElse(0);
    }

    public double getTotalValue() {
        return inventory.stream()
                .mapToDouble(Item::getValue)
                .sum();
    }

    public double getTotalValueWithTax() {
        return getTotalValue() * (1 + taxRate);
    }

    public List<Item> getLowStockItems(int threshold) {
        return inventory.stream()
                .filter(item -> item.quantity < threshold)
                .toList();
    }
}
