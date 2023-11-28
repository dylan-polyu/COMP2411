import java.io.*;
import java.sql.*;
import java.util.Scanner;

import oracle.jdbc.driver.*;

public class OSS {
    // Connection
    private static Statement stmt;
    public static Statement getStmt() { return stmt; }
    public static void setStmt(Statement stmt) { OSS.stmt = stmt; }
    OracleConnection conn;

    public Scanner scanner;
    public OSS() throws SQLException, IOException{
        scanner = new Scanner(System.in);
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        conn = (OracleConnection)DriverManager.getConnection("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms","\"22099885d\"","jnhhpagt");
        setStmt(conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
    }
    public void closeApp() throws SQLException {
        conn.close();
    }
    public void loginAccount() throws SQLException {
        String enteredUsername, enteredPassword;
        ResultSet rset;
        do {
            System.out.print("Input your username: ");
            enteredUsername = scanner.next();
            rset = getStmt().executeQuery("SELECT COUNT(*) FROM USERDATA WHERE userID = '" + enteredUsername + "'");
            if (rset.next() && rset.getInt(1) >= 1) {
                break;
            } else {
                System.out.println("Username does not exist!");
            }
        } while (true);
        do {
            System.out.print("Input password: ");
            enteredPassword = scanner.next();
            rset = getStmt().executeQuery("SELECT password FROM USERDATA WHERE userID = '" + enteredUsername + "'");
            if (rset.next()) {
                String storedPassword = rset.getString(1);
                if (!enteredPassword.equals(storedPassword)) {
                    System.out.println("Incorrect password. Please try again.");
                } else {
                    break;
                }
            }
        } while (true);
    }
    public void createAccount() throws SQLException, IOException {
        Scanner scanner = new Scanner(System.in);
        String enteredUsername, enteredPassword, firstName, lastName, dateOfBirth, email, phoneNumber, address;
        ResultSet rset;
        do {
            boolean checkName = false;
            System.out.print("Input unique username: ");
            enteredUsername = scanner.next();

            rset = getStmt().executeQuery("SELECT COUNT(*) FROM USERDATA WHERE userID = '" + enteredUsername + "'");
            if (rset.next()) {
                if (rset.getInt(1) >= 1) {
                    System.out.println("Username already existed. Try another one.");
                } else {
                    checkName = true;
                }
            }
            if (checkName) {
                break;
            }
        } while (true);
        System.out.print("First name: ");
        firstName = scanner.next();
        System.out.print("Last name: ");
        lastName = scanner.next();
        System.out.print("Date of Birth: ");
        dateOfBirth = scanner.next();
        System.out.print("Email: ");
        email = scanner.next();
        System.out.print("Address: ");
        address = scanner.next();
        System.out.print("Phone number: ");
        phoneNumber = scanner.next();
        System.out.print("Input password: ");
        enteredPassword = scanner.next();
        getStmt().execute(String.format("INSERT INTO USERDATA VALUES('%s','%s','%s','%s',to_date('%s','YYYY-MM-DD'),'%s','%s')", enteredUsername, firstName, lastName, enteredPassword, dateOfBirth, email, phoneNumber));
        getStmt().execute(String.format("INSERT INTO USERADDRESSES VALUES('%s','%s')", enteredUsername, address));
        System.out.println("The account " + enteredUsername + " has been successfully created.");
    }
    public String[] search(String keyword) throws SQLException, IOException {
        ResultSet rset = getStmt().executeQuery("SELECT * FROM products WHERE name LIKE %" + keyword + "% OR description LIKE %" + keyword + "%");
        while(rset.next()) {
            System.out.println(rset.getString(1));
        }
        return null;
    }
}