import java.sql.*;
import java.util.Scanner;

public class BankSystem {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankdb";
    private static final String USER = "root";
    private static final String PASS = "yourpassword";

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    private static void checkBalance(int accId) throws SQLException {
        Connection conn = connect();
        PreparedStatement stmt = conn.prepareStatement("SELECT balance FROM accounts WHERE id = ?");
        stmt.setInt(1, accId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            System.out.println("Your balance: ₹" + rs.getDouble("balance"));
        } else {
            System.out.println("Account not found.");
        }
        conn.close();
    }

    private static void deposit(int accId, double amount) throws SQLException {
        Connection conn = connect();
        PreparedStatement stmt = conn.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE id = ?");
        stmt.setDouble(1, amount);
        stmt.setInt(2, accId);
        int updated = stmt.executeUpdate();
        if (updated > 0) {
            System.out.println("Deposited: ₹" + amount);
        }
        conn.close();
    }

    private static void withdraw(int accId, double amount) throws SQLException {
        Connection conn = connect();
        PreparedStatement stmt = conn.prepareStatement("SELECT balance FROM accounts WHERE id = ?");
        stmt.setInt(1, accId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            double balance = rs.getDouble("balance");
            if (balance >= amount) {
                PreparedStatement updateStmt = conn.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE id = ?");
                updateStmt.setDouble(1, amount);
                updateStmt.setInt(2, accId);
                updateStmt.executeUpdate();
                System.out.println("Withdrawn: ₹" + amount);
            } else {
                System.out.println("Insufficient balance.");
            }
        } else {
            System.out.println("Account not found.");
        }
        conn.close();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int accId = 1; // Demo: assume user is account 1

        while (true) {
            System.out.println("\nATM MENU");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Exit");
            System.out.print("Choose option: ");
            int choice = scanner.nextInt();

            try {
                switch (choice) {
                    case 1: checkBalance(accId); break;
                    case 2: System.out.print("Amount to deposit: "); deposit(accId, scanner.nextDouble()); break;
                    case 3: System.out.print("Amount to withdraw: "); withdraw(accId, scanner.nextDouble()); break;
                    case 4: System.out.println("Thank you!"); System.exit(0); break;
                    default: System.out.println("Invalid choice."); break;
                }
            } catch (SQLException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }
}
