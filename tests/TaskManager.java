
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskManager {

    private final List<String> tasks;
    private String lastAdded;

    public TaskManager() {
        this.tasks = new ArrayList<>();
        this.lastAdded = null;
    }

    public void addTask(String task) {
        if (task == null || task.isBlank()) {
            throw new IllegalArgumentException("task cannot be null or blank");
        }
        tasks.add(task);
        lastAdded = task;
    }

    public boolean contains(String task) {
        if (task == null) {
            return false;
        }
        return tasks.contains(task);
    }

    public boolean removeTask(String task) {
        if (task == null) {
            return false;
        }
        boolean removed = tasks.remove(task);
        if (removed && task.equals(lastAdded)) {
            lastAdded = null;
        }
        return removed;
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
        lastAdded = null;
    }

    public List<String> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(tasks));
    }

    public String getLastAdded() {
        return lastAdded;
    }
}
