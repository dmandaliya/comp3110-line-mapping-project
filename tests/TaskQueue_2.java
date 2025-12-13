
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class TaskQueue {

    private Queue<Task> queue;

    public TaskQueue() {
        this.queue = new PriorityQueue<>(Comparator.comparingInt(t -> -t.priority));
    }

    static class Task {
        String name;
        int priority;
        long timestamp;

        Task(String name, int priority) {
            this.name = name;
            this.priority = priority;
            this.timestamp = System.currentTimeMillis();
        }

        public String getName() {
            return name;
        }

        public int getPriority() {
            return priority;
        }
    }

    public void enqueue(String name, int priority) {
        if (priority < 1 || priority > 10) {
            throw new IllegalArgumentException("Priority must be between 1 and 10");
        }
        queue.offer(new Task(name, priority));
    }

    public Task dequeue() {
        return queue.poll();
    }

    public Task peek() {
        return queue.peek();
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void clear() {
        queue.clear();
    }
}
