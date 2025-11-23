import java.util.ArrayList;
import java.util.List;

public class Member {

    private int memberId;
    private String name;
    private String email;
    private List<Integer> issuedBooks;

    public Member(int memberId, String name, String email) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.issuedBooks = new ArrayList<>();
    }

    public int getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<Integer> getIssuedBooks() {
        return issuedBooks;
    }

    public void addIssuedBook(int bookId) {
        if (!issuedBooks.contains(bookId)) {
            issuedBooks.add(bookId);
        }
    }

    public void returnIssuedBook(int bookId) {
        issuedBooks.remove(Integer.valueOf(bookId));
    }

    public void displayMemberDetails() {
        System.out.println("Member ID : " + memberId);
        System.out.println("Name      : " + name);
        System.out.println("Email     : " + email);
        System.out.println("Issued IDs: " + (issuedBooks.isEmpty() ? "None" : issuedBooks));
        System.out.println("----------------------------------");
    }

    // Serialize to CSV: memberId,name,email,issued1;issued2;...
    public String toCSV() {
        String n = name.replace(",", " ");
        String e = email.replace(",", " ");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < issuedBooks.size(); i++) {
            if (i > 0) sb.append(";");
            sb.append(issuedBooks.get(i));
        }

        return memberId + "," + n + "," + e + "," + sb.toString();
    }

    public static Member fromCSV(String line) {
        // Format: id,name,email,issued1;issued2;...
        String[] parts = line.split(",", 4);
        if (parts.length < 3) return null;

        int id = Integer.parseInt(parts[0].trim());
        String name = parts[1].trim();
        String email = parts[2].trim();

        Member m = new Member(id, name, email);

        if (parts.length == 4 && !parts[3].trim().isEmpty()) {
            String[] ids = parts[3].split(";");
            for (String s : ids) {
                try {
                    int bid = Integer.parseInt(s.trim());
                    m.addIssuedBook(bid);
                } catch (NumberFormatException ignore) {
                }
            }
        }
        return m;
    }
}

