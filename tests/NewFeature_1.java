public class NewFeature {

    private int counter;
    private String status;

    public NewFeature() {
        this.counter = 0;
        this.status = "INIT";
    }

    public void increment() {
        counter++;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isReady() {
        return counter > 5;
    }

    public void reset() {
        counter = 0;
        status = "RESET";
    }

    public String getStatus() {
        return status;
    }

    public int getCounter() {
        return counter;
    }
}
