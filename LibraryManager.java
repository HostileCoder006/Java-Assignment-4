import java.io.*;
import java.util.*;

public class LibraryManager {

    private Map<Integer, Book> books = new HashMap<>();
    private Map<Integer, Member> members = new HashMap<>();
    private Scanner sc = new Scanner(System.in);

    private static final String BOOKS_FILE = "books.txt";
    private static final String MEMBERS_FILE = "members.txt";

    public static void main(String[] args) {
        LibraryManager lib = new LibraryManager();
        lib.loadFromFile();
        lib.menu();
        lib.saveToFile();
        System.out.println("Saved data and exiting. Goodbye!");
    }

    private void menu() {
        int choice = 0;

        do {
            System.out.println("\n===== City Library Digital Management System =====");
            System.out.println("1. Add Book");
            System.out.println("2. Add Member");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. Search Books (title/author/category)");
            System.out.println("6. Sort Books (by title)");
            System.out.println("7. Sort Books (by author)");
            System.out.println("8. View All Books");
            System.out.println("9. View All Members");
            System.out.println("10. Exit");
            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            switch (choice) {
                case 1 -> addBook();
                case 2 -> addMember();
                case 3 -> issueBook();
                case 4 -> returnBook();
                case 5 -> searchBooks();
                case 6 -> sortBooksByTitle();
                case 7 -> sortBooksByAuthor();
                case 8 -> viewAllBooks();
                case 9 -> viewAllMembers();
                case 10 -> {
                    // exit
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        } while (choice != 10);
    }

    private void addBook() {
        try {
            System.out.print("Enter Book ID (integer): ");
            int id = Integer.parseInt(sc.nextLine().trim());

            if (books.containsKey(id)) {
                System.out.println("Book ID already exists. Choose a unique ID.");
                return;
            }

            System.out.print("Enter Title: ");
            String title = sc.nextLine().trim();

            System.out.print("Enter Author: ");
            String author = sc.nextLine().trim();

            System.out.print("Enter Category: ");
            String category = sc.nextLine().trim();

            Book b = new Book(id, title, author, category);
            books.put(id, b);
            saveToFile();
            System.out.println("Book added successfully with ID: " + id);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        } catch (Exception e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    private void addMember() {
        try {
            System.out.print("Enter Member ID (integer): ");
            int id = Integer.parseInt(sc.nextLine().trim());

            if (members.containsKey(id)) {
                System.out.println("Member ID already exists. Choose a unique ID.");
                return;
            }

            System.out.print("Enter Name: ");
            String name = sc.nextLine().trim();

            System.out.print("Enter Email: ");
            String email = sc.nextLine().trim();

            Member m = new Member(id, name, email);
            members.put(id, m);
            saveToFile();
            System.out.println("Member added successfully with ID: " + id);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        } catch (Exception e) {
            System.out.println("Error adding member: " + e.getMessage());
        }
    }

    private void issueBook() {
        try {
            System.out.print("Enter Book ID to issue: ");
            int bookId = Integer.parseInt(sc.nextLine().trim());

            System.out.print("Enter Member ID: ");
            int memberId = Integer.parseInt(sc.nextLine().trim());

            if (!books.containsKey(bookId)) {
                System.out.println("Book not found.");
                return;
            }

            Book book = books.get(bookId);
            if (book.isIssued()) {
                System.out.println("Book is already issued.");
                return;
            }

            if (!members.containsKey(memberId)) {
                System.out.println("Member not found.");
                return;
            }

            book.markAsIssued();
            members.get(memberId).addIssuedBook(bookId);
            saveToFile();
            System.out.println("Book issued successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        }
    }

    private void returnBook() {
        try {
            System.out.print("Enter Book ID to return: ");
            int bookId = Integer.parseInt(sc.nextLine().trim());

            if (!books.containsKey(bookId)) {
                System.out.println("Book not found.");
                return;
            }

            Book book = books.get(bookId);
            if (!book.isIssued()) {
                System.out.println("Book is not currently issued.");
                return;
            }

            // remove from any member who has it
            for (Member m : members.values()) {
                if (m.getIssuedBooks().contains(bookId)) {
                    m.returnIssuedBook(bookId);
                }
            }

            book.markAsReturned();
            saveToFile();
            System.out.println("Book returned successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        }
    }

    private void searchBooks() {
        System.out.print("Enter search keyword (title/author/category): ");
        String key = sc.nextLine().trim().toLowerCase();

        boolean found = false;

        for (Book b : books.values()) {
            if (b.getTitle().toLowerCase().contains(key)
                    || b.getAuthor().toLowerCase().contains(key)
                    || b.getCategory().toLowerCase().contains(key)) {
                b.displayBookDetails();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No matching books found.");
        }
    }

    private void sortBooksByTitle() {
        List<Book> list = new ArrayList<>(books.values());
        Collections.sort(list); // uses Comparable (title)
        System.out.println("Books sorted by title:");
        list.forEach(Book::displayBookDetails);
    }

    private void sortBooksByAuthor() {
        List<Book> list = new ArrayList<>(books.values());
        list.sort(Comparator.comparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER));
        System.out.println("Books sorted by author:");
        list.forEach(Book::displayBookDetails);
    }

    private void viewAllBooks() {
        if (books.isEmpty()) {
            System.out.println("No books available.");
            return;
        }

        for (Book b : books.values()) {
            b.displayBookDetails();
        }
    }

    private void viewAllMembers() {
        if (members.isEmpty()) {
            System.out.println("No members registered.");
            return;
        }

        for (Member m : members.values()) {
            m.displayMemberDetails();
        }
    }

    // Save books and members to files (text CSV)
    public void saveToFile() {
        saveBooks();
        saveMembers();
    }

    private void saveBooks() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(BOOKS_FILE))) {
            for (Book b : books.values()) {
                bw.write(b.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing books file: " + e.getMessage());
        }
    }

    private void saveMembers() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MEMBERS_FILE))) {
            for (Member m : members.values()) {
                bw.write(m.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing members file: " + e.getMessage());
        }
    }

    // Load data from files at startup
    public void loadFromFile() {
        loadBooks();
        loadMembers();
    }

    private void loadBooks() {
        File f = new File(BOOKS_FILE);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                Book b = Book.fromCSV(line);
                if (b != null) {
                    books.put(b.getBookId(), b);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading books file: " + e.getMessage());
        }
    }

    private void loadMembers() {
        File f = new File(MEMBERS_FILE);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                Member m = Member.fromCSV(line);
                if (m != null) {
                    members.put(m.getMemberId(), m);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading members file: " + e.getMessage());
        }
    }
}
