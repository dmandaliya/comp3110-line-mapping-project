
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EventScheduler {

    private List<Event> events;

    public EventScheduler() {
        this.events = new ArrayList<>();
    }

    static class Event {
        String name;
        LocalDate date;
        LocalTime time;
        String location;

        Event(String name, LocalDate date, LocalTime time, String location) {
            this.name = name;
            this.date = date;
            this.time = time;
            this.location = location;
        }
    }

    public void addEvent(String name, LocalDate date, LocalTime time, String location) {
        events.add(new Event(name, date, time, location));
    }

    public List<Event> getEvents() {
        return new ArrayList<>(events);
    }

    public List<Event> getUpcomingEvents() {
        LocalDate today = LocalDate.now();
        return events.stream()
                .filter(event -> !event.date.isBefore(today))
                .sorted(Comparator.comparing(event -> event.date))
                .collect(Collectors.toList());
    }

    public List<Event> getEventsOnDate(LocalDate date) {
        return events.stream()
                .filter(event -> event.date.equals(date))
                .collect(Collectors.toList());
    }

    public void removeEvent(String name) {
        events.removeIf(event -> event.name.equals(name));
    }

    public int getEventCount() {
        return events.size();
    }
}
