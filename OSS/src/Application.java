import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) throws SQLException, IOException {
        Scanner scanner = new Scanner(System.in);
        OSS oss = new OSS();

        while (true) {
            System.out.println("========================================");
            System.out.println("Welcome to PolyShop, a friendly online shopping system");
            System.out.println("========================================");
            System.out.println("Please choose from the following options:");
            System.out.println("1. Login");
            System.out.println("2. Create Account");
            System.out.println("0. Close App");
            System.out.print(">> ");
            String input = scanner.nextLine();
            System.out.println();
            if (input.equals("0")) {
                oss.closeApp();
                break;
            }
            switch (input) {
                case "1":
                    oss.loginAccount();
                    break;
                case "2":
                    oss.createAccount();
                    break;
                default:
                    System.out.println("Invalid input. Please enter a valid option.");
                    break;
            }
            System.out.println();
        }
    }
}