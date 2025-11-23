import java.io.Serializable;

public class Book implements Serializable, Comparable<Book> {

    private int bookId;
    private String title;
    private String author;
    private String category;
    private boolean isIssued;

    public Book(int bookId, String title, String author, String category, boolean isIssued) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.isIssued = isIssued;
    }

    public Book(int bookId, String title, String author, String category) {
        this(bookId, title, author, category, false);
    }

    public int getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public boolean isIssued() {
        return isIssued;
    }

    public void markAsIssued() {
        this.isIssued = true;
    }

    public void markAsReturned() {
        this.isIssued = false;
    }

    public void displayBookDetails() {
        System.out.println("Book ID   : " + bookId);
        System.out.println("Title     : " + title);
        System.out.println("Author    : " + author);
        System.out.println("Category  : " + category);
        System.out.println("Issued    : " + (isIssued ? "Yes" : "No"));
        System.out.println("----------------------------------");
    }

    // For saving to CSV (bookId,title,author,category,isIssued)
    public String toCSV() {
        // Escape commas in a simple way
        String t = title.replace(",", " ");
        String a = author.replace(",", " ");
        String c = category.replace(",", " ");
        return bookId + "," + t + "," + a + "," + c + "," + isIssued;
    }

    public static Book fromCSV(String line) {
        // Expected format: id,title,author,category,isIssued
        String[] parts = line.split(",", 5);
        if (parts.length < 5) return null;

        int id = Integer.parseInt(parts[0].trim());
        String title = parts[1].trim();
        String author = parts[2].trim();
        String category = parts[3].trim();
        boolean issued = Boolean.parseBoolean(parts[4].trim());

        return new Book(id, title, author, category, issued);
    }

    @Override
    public int compareTo(Book other) {
        return this.title.compareToIgnoreCase(other.title);
    }
}
