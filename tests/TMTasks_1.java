
import java.util.ArrayList;
import java.util.List;

public class TMTasks_1 {

    private final List<String> tasks;

    public TMTasks_1() {
        this.tasks = new ArrayList<>();
    }

    public void addTask(String task) {
        if (task == null) {
            throw new IllegalArgumentException("task cannot be null");
        }
        tasks.add(task);
    }

    public boolean removeTask(String task) {
        if (task == null) {
            return false;
        }
        return tasks.remove(task);
    }

    public String getTask(int index) {
        if (index < 0 || index >= tasks.size()) {
            return null;
        }
        return tasks.get(index);
    }

    public int size() {
        return tasks.size();
    }

    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    public void clear() {
        tasks.clear();
    }

    public List<String> snapshot() {
        return new ArrayList<>(tasks);
    }
}
