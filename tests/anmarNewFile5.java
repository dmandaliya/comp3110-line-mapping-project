class FactorialCalculatorNew {

    public int factorial(int n) {
        if (n < 0) throw new IllegalArgumentException("Negative number");
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }

    public long factorialIterative(int n) {
        if (n < 0) throw new IllegalArgumentException("Negative number");
        long result = 1;
        for (int i = 2; i <= n; i++) result *= i;
        return result;
    }
}
