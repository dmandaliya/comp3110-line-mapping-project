
import java.util.HashMap;
import java.util.Map;

public class StudentGrades {

    private Map<String, Double> grades;

    public StudentGrades() {
        this.grades = new HashMap<>();
    }

    public void addGrade(String student, double grade) {
        grades.put(student, grade);
    }

    public double getGrade(String student) {
        return grades.getOrDefault(student, 0.0);
    }

    public double getAverage() {
        if (grades.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (double grade : grades.values()) {
            sum += grade;
        }
        return sum / grades.size();
    }

    public String getHighestStudent() {
        String best = null;
        double highest = -1;
        for (Map.Entry<String, Double> entry : grades.entrySet()) {
            if (entry.getValue() > highest) {
                highest = entry.getValue();
                best = entry.getKey();
            }
        }
        return best;
    }
}
