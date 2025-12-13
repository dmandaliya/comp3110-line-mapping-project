
import java.util.ArrayList;
import java.util.List;

public class SearchEngine {

    private List<Document> documents;

    public SearchEngine() {
        this.documents = new ArrayList<>();
    }

    public void addDocument(String id, String content) {
        documents.add(new Document(id, content));
    }

    public List<Document> search(String query) {
        List<Document> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (Document doc : documents) {
            if (doc.content.toLowerCase().contains(lowerQuery)) {
                results.add(doc);
            }
        }
        return results;
    }

    public int getDocumentCount() {
        return documents.size();
    }

    static class Document {
        String id;
        String content;

        Document(String id, String content) {
            this.id = id;
            this.content = content;
        }
    }
}
