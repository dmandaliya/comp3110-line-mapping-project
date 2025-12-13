
import java.util.LinkedList;
import java.util.Queue;

public class TaskQueue {

    private Queue<Task> queue;

    public TaskQueue() {
        this.queue = new LinkedList<>();
    }

    public void enqueue(String name, int priority) {
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

    static class Task {
        String name;
        int priority;

        Task(String name, int priority) {
            this.name = name;
            this.priority = priority;
        }
    }
}
