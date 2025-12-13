
import java.util.ArrayList;
import java.util.List;

public class BookLibrary {

    private List<Book> books;

    public BookLibrary() {
        this.books = new ArrayList<>();
    }

    public void addBook(String title, String author, int year) {
        books.add(new Book(title, author, year));
    }

    public Book findByTitle(String title) {
        for (Book book : books) {
            if (book.title.equals(title)) {
                return book;
            }
        }
        return null;
    }

    public List<Book> findByAuthor(String author) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.author.equals(author)) {
                result.add(book);
            }
        }
        return result;
    }

    public int getTotalBooks() {
        return books.size();
    }

    static class Book {
        String title;
        String author;
        int year;

        Book(String title, String author, int year) {
            this.title = title;
            this.author = author;
            this.year = year;
        }
    }
}
