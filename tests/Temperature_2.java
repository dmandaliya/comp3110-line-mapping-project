
public class Temperature {

    private double celsius;

    public Temperature(double celsius) {
        if (celsius < -273.15) {
            throw new IllegalArgumentException("Temperature cannot be below absolute zero");
        }
        this.celsius = celsius;
    }

    public double getCelsius() {
        return celsius;
    }

    public void setCelsius(double celsius) {
        if (celsius < -273.15) {
            throw new IllegalArgumentException("Temperature cannot be below absolute zero");
        }
        this.celsius = celsius;
    }

    public double toFahrenheit() {
        return (celsius * 9.0 / 5.0) + 32.0;
    }

    public double toKelvin() {
        return celsius + 273.15;
    }

    public double toRankine() {
        return toFahrenheit() + 459.67;
    }

    public boolean isFreezing() {
        return celsius <= 0;
    }

    public boolean isBoiling() {
        return celsius >= 100;
    }

    public String getDescription() {
        if (celsius < 0) return "Freezing";
        if (celsius < 10) return "Cold";
        if (celsius < 20) return "Cool";
        if (celsius < 30) return "Warm";
        return "Hot";
    }
}
