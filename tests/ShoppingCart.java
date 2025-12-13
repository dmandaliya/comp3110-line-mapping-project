
import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    private List<CartItem> items;
    private double discount;

    public ShoppingCart() {
        this.items = new ArrayList<>();
        this.discount = 0.0;
    }

    public void addItem(String product, double price, int quantity) {
        items.add(new CartItem(product, price, quantity));
    }

    public void removeItem(String product) {
        items.removeIf(item -> item.product.equals(product));
    }

    public double calculateTotal() {
        double total = 0.0;
        for (CartItem item : items) {
            total += item.price * item.quantity;
        }
        return total - (total * discount);
    }

    public void applyDiscount(double percentage) {
        this.discount = percentage / 100.0;
    }

    public int getItemCount() {
        return items.size();
    }

    private static class CartItem {
        String product;
        double price;
        int quantity;

        CartItem(String product, double price, int quantity) {
            this.product = product;
            this.price = price;
            this.quantity = quantity;
        }
    }
}
