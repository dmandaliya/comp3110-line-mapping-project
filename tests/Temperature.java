
public class Temperature {

    private double celsius;

    public Temperature(double celsius) {
        this.celsius = celsius;
    }

    public double getCelsius() {
        return celsius;
    }

    public void setCelsius(double celsius) {
        this.celsius = celsius;
    }

    public double toFahrenheit() {
        return (celsius * 9.0 / 5.0) + 32.0;
    }

    public double toKelvin() {
        return celsius + 273.15;
    }

    public boolean isFreezing() {
        return celsius <= 0;
    }

    public boolean isBoiling() {
        return celsius >= 100;
    }
}
