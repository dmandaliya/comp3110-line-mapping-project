class CalculatorNew {

    // Constructor
    public CalculatorNew() {
        // Initialization
    }

    // Addition method
    public int add(int a, int b) {
        return a + b;
    }

    // Subtraction method
    public int subtract(int a, int b) {
        int difference = a - b;
        return difference;
    }

    public int multiply(int a, int b) {
        return a * b;
    }

    public int divide(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("Cannot divide by zero");
        }
        return a / b;
    }

    public int modulus(int a, int b) {
        return a % b;
    }

    public void printResult(int result) {
        System.out.println("Result: " + result);
    }

}
