class RectangleNew {
    // Properties
    private int width;
    private int height;

    // validatiion
    public RectangleNew(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Dimensions must be positive");
        }
        this.width = width;
        this.height = height;
    }

    // Get area
    public int area() {
        return width * height;
    }

    // Get perimeter
    public int perimeter() {
        int result = 2 * (width + height);
        return result;
    }

    // Check if square
    public boolean isSquare() {
        return width == height;
    }

    // Get diagonal length
    public double diagonal() {
        return Math.sqrt(width * width + height * height);
    }

    // To string representation
    @Override
    public String toString() {
        return "Rectangle[width=" + width + ", height=" + height + "]";
    }
}
