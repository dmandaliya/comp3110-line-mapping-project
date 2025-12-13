
public class PaymentProcessor {

    public boolean processPayment(double amount) {
        if (amount <= 0) {
            return false;
        }
        return true;
    }

    public double calculateFee(double amount) {
        return amount * 0.029 + 0.30;
    }

    public double getNetAmount(double amount) {
        return amount - calculateFee(amount);
    }

    public boolean refund(double amount) {
        if (amount <= 0) {
            return false;
        }
        return true;
    }
}
