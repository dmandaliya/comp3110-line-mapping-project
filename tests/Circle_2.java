
public class Circle {

    private double radius;
    private static final double PI = Math.PI;

    public Circle(double radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive");
        }
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive");
        }
        this.radius = radius;
    }

    public double getDiameter() {
        return 2 * radius;
    }

    public double getCircumference() {
        return 2 * PI * radius;
    }

    public double getArea() {
        return PI * radius * radius;
    }

    public void scale(double factor) {
        if (factor <= 0) {
            throw new IllegalArgumentException("Scale factor must be positive");
        }
        this.radius *= factor;
    }

    public boolean contains(double x, double y, double centerX, double centerY) {
        double dx = x - centerX;
        double dy = y - centerY;
        return (dx * dx + dy * dy) <= (radius * radius);
    }

    @Override
    public String toString() {
        return String.format("Circle(radius=%.2f, area=%.2f)", radius, getArea());
    }
}
