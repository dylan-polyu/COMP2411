import java.io.*;
import java.sql.*;
import java.util.Scanner;

import oracle.jdbc.driver.*;

public class OSS {
    // Connection
    private static Statement stmt;
    public static Statement getStmt() { return stmt; }
    public static void setStmt(Statement stmt) { OSS.stmt = stmt; }
    public OSS() throws SQLException, IOException{
       DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
       OracleConnection conn = (OracleConnection)DriverManager.getConnection("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms","\"22099885d\"","jnhhpagt");
       setStmt(conn.createStatement());
    }
    public static void loginAccount() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String enteredUsername = "";
        String enteredPassword = "";
        String storedPassword = "";
        ResultSet rset = null;
        do {
            System.out.print("Input username: ");
            enteredUsername = scanner.next();
            rset = getStmt().executeQuery("SELECT COUNT(*) FROM user where username = '" + enteredUsername + "'");
            if(rset.getInt(1) == 0) {
                System.out.println(enteredUsername + " does not exists.");
            }
        } while (rset.getInt(1) == 0);
        do {
            System.out.print("Input password: ");
            enteredPassword = scanner.next();
            rset = getStmt().executeQuery("SELECT password FROM user where username = '" + enteredUsername + "'");
            storedPassword = rset.getString(1);
            if(!enteredPassword.equals(storedPassword)) {
                System.out.println("Incorrect password. Please try again.");
            }
        } while (!enteredPassword.equals(storedPassword));
    }
    public static void createAccount() throws SQLException, IOException{
        Scanner scanner = new Scanner(System.in);
        String enteredUsername = "";
        String enteredPassword = "";
        ResultSet rset = null;
        do {
            System.out.print("Input username: ");
            enteredUsername = scanner.next();
            rset = getStmt().executeQuery("SELECT COUNT(*) FROM user where username = '" + enteredUsername + "'");
            if(rset.getInt(1) >= 1) {
                System.out.println(enteredUsername + " already existed. Try another one.");
            }
        } while (rset.getInt(1) >= 1);
        System.out.print("Input password: ");
        enteredPassword = scanner.next();
        getStmt().execute("INSERT INTO user\nVALUES('" + enteredUsername + "', '" + enteredPassword + "')");
        System.out.println("User account created.");
    }
    public static String[] search(String keyword) throws SQLException, IOException {
        ResultSet rset = getStmt().executeQuery("SELECT * FROM products WHERE name LIKE %" + keyword + "% OR description LIKE %" + keyword + "%");
        while(rset.next()) {
            System.out.println(rset.getString(1));
        }
        return null;
    }
}
