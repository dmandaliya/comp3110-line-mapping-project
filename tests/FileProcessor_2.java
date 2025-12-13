
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileProcessor {

    public List<String> readLines(String filename) throws IOException {
        Path path = Paths.get(filename);
        return Files.readAllLines(path);
    }

    public void writeLines(String filename, List<String> lines) throws IOException {
        Path path = Paths.get(filename);
        Files.write(path, lines);
    }

    public int countLines(String filename) throws IOException {
        Path path = Paths.get(filename);
        try (var stream = Files.lines(path)) {
            return (int) stream.count();
        }
    }

    public List<String> filterLines(String filename, String pattern) throws IOException {
        return readLines(filename).stream()
                .filter(line -> line.contains(pattern))
                .collect(Collectors.toList());
    }

    public void appendLine(String filename, String line) throws IOException {
        Path path = Paths.get(filename);
        List<String> lines = new ArrayList<>(readLines(filename));
        lines.add(line);
        Files.write(path, lines);
    }
}
