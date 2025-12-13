
import java.util.*;
import java.util.stream.Collectors;

public class SearchEngine {

    private Map<String, Document> documents;
    private Map<String, Set<String>> invertedIndex;

    public SearchEngine() {
        this.documents = new HashMap<>();
        this.invertedIndex = new HashMap<>();
    }

    static class Document {
        String id;
        String content;
        double score;

        Document(String id, String content) {
            this.id = id;
            this.content = content;
            this.score = 0.0;
        }
    }

    public void addDocument(String id, String content) {
        Document doc = new Document(id, content);
        documents.put(id, doc);
        indexDocument(id, content);
    }

    private void indexDocument(String id, String content) {
        String[] words = content.toLowerCase().split("\\W+");
        for (String word : words) {
            if (word.isEmpty()) continue;
            invertedIndex.computeIfAbsent(word, k -> new HashSet<>()).add(id);
        }
    }

    public List<Document> search(String query) {
        String[] queryWords = query.toLowerCase().split("\\W+");
        Map<String, Integer> docScores = new HashMap<>();
        
        for (String word : queryWords) {
            Set<String> docIds = invertedIndex.getOrDefault(word, Collections.emptySet());
            for (String docId : docIds) {
                docScores.put(docId, docScores.getOrDefault(docId, 0) + 1);
            }
        }
        
        return docScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(entry -> documents.get(entry.getKey()))
                .collect(Collectors.toList());
    }

    public void removeDocument(String id) {
        documents.remove(id);
        invertedIndex.values().forEach(set -> set.remove(id));
    }

    public int getDocumentCount() {
        return documents.size();
    }
}
