
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
        if (min > max) {
            throw new IllegalArgumentException("min > max");
        }
        return value >= min && value <= max;
    }

    public int clampToNonNegative(int value) {
        return clamp(value, 0, Integer.MAX_VALUE);
    }
}
