class RectangleOld {
    // Dimensions
    public int width;
    public int height;

    // Constructor
    public RectangleOld(int width, int height) {
        this.width = width;
        this.height = height;
    }

    // Calculate area
    public int calculateArea() {
        int area = width * height;
        return area;
    }

    // Calculate perimeter
    public int calculatePerimeter() {
        return 2 * (width + height);
    }

    // Display info
    public void display() {
        System.out.println("Width: " + width + ", Height: " + height);
    }
}
