
import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    private List<CartItem> items;
    private double discountRate;
    private double taxRate;

    public ShoppingCart() {
        this.items = new ArrayList<>();
        this.discountRate = 0.0;
        this.taxRate = 0.0;
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

        double getSubtotal() {
            return price * quantity;
        }
    }

    public void addItem(String product, double price, int quantity) {
        CartItem existing = findItem(product);
        if (existing != null) {
            existing.quantity += quantity;
        } else {
            items.add(new CartItem(product, price, quantity));
        }
    }

    private CartItem findItem(String product) {
        for (CartItem item : items) {
            if (item.product.equals(product)) {
                return item;
            }
        }
        return null;
    }

    public void removeItem(String product) {
        items.removeIf(item -> item.product.equals(product));
    }

    public void applyDiscount(double percentage) {
        this.discountRate = percentage / 100.0;
    }

    public void setTaxRate(double percentage) {
        this.taxRate = percentage / 100.0;
    }

    public double calculateSubtotal() {
        double subtotal = 0.0;
        for (CartItem item : items) {
            subtotal += item.getSubtotal();
        }
        return subtotal;
    }

    public double calculateTotal() {
        double subtotal = calculateSubtotal();
        double afterDiscount = subtotal - (subtotal * discountRate);
        return afterDiscount + (afterDiscount * taxRate);
    }

    public int getItemCount() {
        return items.size();
    }
}
