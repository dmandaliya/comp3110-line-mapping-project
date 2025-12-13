
public class PaymentProcessor {

    private static final double FEE_PERCENTAGE = 0.029;
    private static final double FIXED_FEE = 0.30;
    private double totalProcessed;

    public PaymentProcessor() {
        this.totalProcessed = 0.0;
    }

    public double calculateFee(double amount) {
        return amount * FEE_PERCENTAGE + FIXED_FEE;
    }

    public double getNetAmount(double amount) {
        return amount - calculateFee(amount);
    }

    public boolean processPayment(double amount, String currency) {
        if (amount <= 0) {
            return false;
        }
        if (!isValidCurrency(currency)) {
            return false;
        }
        totalProcessed += amount;
        return true;
    }

    private boolean isValidCurrency(String currency) {
        return currency != null && 
               (currency.equals("USD") || currency.equals("EUR") || currency.equals("GBP"));
    }

    public boolean refund(double amount) {
        if (amount <= 0 || amount > totalProcessed) {
            return false;
        }
        totalProcessed -= amount;
        return true;
    }

    public double getTotalProcessed() {
        return totalProcessed;
    }

    public void reset() {
        totalProcessed = 0.0;
    }
}
