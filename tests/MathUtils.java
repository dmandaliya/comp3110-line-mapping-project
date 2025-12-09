package mytool.dataset;

public class MathUtils {

    public int clamp(int value, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min > max");
        }
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public boolean isBetweenInclusive(int value, int min, int max) {
        return value >= min && value <= max;
    }
}
