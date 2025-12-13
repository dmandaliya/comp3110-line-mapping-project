
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventScheduler {

    private List<Event> events;

    public EventScheduler() {
        this.events = new ArrayList<>();
    }

    public void addEvent(String name, LocalDate date) {
        events.add(new Event(name, date));
    }

    public List<Event> getEvents() {
        return new ArrayList<>(events);
    }

    public List<Event> getUpcomingEvents() {
        LocalDate today = LocalDate.now();
        List<Event> upcoming = new ArrayList<>();
        for (Event event : events) {
            if (event.date.isAfter(today) || event.date.isEqual(today)) {
                upcoming.add(event);
            }
        }
        return upcoming;
    }

    public void removeEvent(String name) {
        events.removeIf(event -> event.name.equals(name));
    }

    static class Event {
        String name;
        LocalDate date;

        Event(String name, LocalDate date) {
            this.name = name;
            this.date = date;
        }
    }
}
