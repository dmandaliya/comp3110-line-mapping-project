
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BookLibrary {

    private List<Book> books;

    public BookLibrary() {
        this.books = new ArrayList<>();
    }

    static class Book {
        String title;
        String author;
        int year;
        String isbn;

        Book(String title, String author, int year, String isbn) {
            this.title = title;
            this.author = author;
            this.year = year;
            this.isbn = isbn;
        }
    }

    public void addBook(String title, String author, int year, String isbn) {
        books.add(new Book(title, author, year, isbn));
    }

    public void removeBook(String isbn) {
        books.removeIf(book -> book.isbn.equals(isbn));
    }

    public Book findByTitle(String title) {
        return books.stream()
                .filter(book -> book.title.equalsIgnoreCase(title))
                .findFirst()
                .orElse(null);
    }

    public Book findByIsbn(String isbn) {
        return books.stream()
                .filter(book -> book.isbn.equals(isbn))
                .findFirst()
                .orElse(null);
    }

    public List<Book> findByAuthor(String author) {
        return books.stream()
                .filter(book -> book.author.equalsIgnoreCase(author))
                .collect(Collectors.toList());
    }

    public List<Book> getBooksSortedByYear() {
        return books.stream()
                .sorted(Comparator.comparingInt(book -> book.year))
                .collect(Collectors.toList());
    }

    public int getTotalBooks() {
        return books.size();
    }
}
