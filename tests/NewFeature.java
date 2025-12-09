public class NewFeature {

    private int value;
    private String state;

    public NewFeature() {
        this.value = 0;
        this.state = "INITIALIZED";
    }

    public void increase() {
        value++;
    }

    public void updateState(String state) {
        this.state = state;
    }

    public boolean isReady() {
        return value >= 5;
    }

    public void clear() {
        value = 0;
        state = "CLEARED";
    }

    public String getState() {
        return state;
    }

    public int getValue() {
        return value;
    }
}
