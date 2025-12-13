
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileProcessor {

    public List<String> readLines(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        reader.close();
        return lines;
    }

    public void writeLines(String filename, List<String> lines) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
        writer.close();
    }

    public int countLines(String filename) throws IOException {
        return readLines(filename).size();
    }
}
