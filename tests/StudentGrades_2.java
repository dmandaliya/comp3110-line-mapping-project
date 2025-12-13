
import java.util.*;
import java.util.stream.Collectors;

public class StudentGrades {

    private Map<String, Double> grades;

    public StudentGrades() {
        this.grades = new HashMap<>();
    }

    public void addGrade(String student, double grade) {
        if (grade < 0 || grade > 100) {
            throw new IllegalArgumentException("Grade must be between 0 and 100");
        }
        grades.put(student, grade);
    }

    public double getGrade(String student) {
        return grades.getOrDefault(student, 0.0);
    }

    public String getHighestStudent() {
        return grades.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public String getLowestStudent() {
        return grades.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public double getAverage() {
        if (grades.isEmpty()) {
            return 0.0;
        }
        return grades.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    public List<String> getPassingStudents(double passingGrade) {
        return grades.entrySet().stream()
                .filter(entry -> entry.getValue() >= passingGrade)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

    public int getStudentCount() {
        return grades.size();
    }
}
