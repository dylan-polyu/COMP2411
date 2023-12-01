import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class UserApp {
    Scanner scanner;
    OSS oss;
    public UserApp(OSS oss) throws SQLException {
        scanner = new Scanner(System.in);
        this.oss = oss;
    }
    public void run() throws SQLException {
        System.out.println();
        System.out.println("=======================================================");
        System.out.println("Welcome to PolyShop, an online-friendly shopping system");
        System.out.println("=======================================================");
        boolean flag = true;
        while(true){
            System.out.println("\nPlease choose from the following options:");
            System.out.println("1. Display All Products");
            System.out.println("2. Filter Products");
            System.out.println("3. Search Products");
            System.out.println("4. View Cart");
            System.out.println("5. Edit Profile");
            System.out.println("0. Log Out");
            System.out.print(">> ");
            String input = scanner.next();
            if(input.equals("0")) {
                System.out.println("Thank you for visiting. See you again.");
                return;
            }
            switch (input) {
                case "1":
                    flag = oss.displayProduct();
                    break;
                case "2":
                    flag = oss.filterProduct();
                    break;
                case "3":
                    flag = oss.searchProduct();
                    break;
                case "4":
                    flag = oss.viewCart();
                    break;
                case "5":
                    flag = oss.editProfile();
                    break;
                default:
                    System.out.println("Invalid input. Please enter a valid option.");
                    continue;
            }
            if(!flag) { continue; }
            String key = input;
            do {
                System.out.println("\nPlease choose from the following options:");
                System.out.println("1. Product Details");
                System.out.println("2. Checkout Cart");
                System.out.println("0. Back");
                System.out.print(">> ");
                input = scanner.next();
                switch (input) {
                    case "0":
                        break;
                    case "1":
                        if (oss.productDetails()) {
                            do {
                                System.out.println("\nPlease choose from the following options:");
                                if (key.equals("4")) {
                                    System.out.println("1. Remove From Cart");
                                } else {
                                    System.out.println("1. Add To Cart");
                                }
                                System.out.println("2. Check Review");
                                System.out.println("3. Add a Review");
                                System.out.println("0. Back");
                                System.out.print(">> ");
                                input = scanner.next();
                                switch (input) {
                                    case "0":
                                        break;
                                    case "1":
                                        if (key.equals("4")) {
                                            oss.removeFromCart();
                                        } else {
                                            oss.addToCart();
                                        }
                                        input = "0";
                                        break;
                                    case "2":
                                        if (oss.printreview()) {
                                            // Handle review printing logic
                                        } else {
                                            System.out.println("No reviews available.");
                                        }
                                        break;
                                    case "3":
                                        if (!oss.addReview()) {
                                            System.out.println("Cancelled.");
                                        }
                                        break;
                                    default:
                                        System.out.println("Invalid input. Please enter a valid option.");
                                        break;
                                }
                            } while (!input.equals("0"));
                        }
                        break;
                    case "2":
                        if (oss.checkout()) {
                            oss.bill();
                        }
                        input = "0";
                        break;
                    default:
                        System.out.println("Invalid input. Please enter a valid option.");
                        break;
                }
            } while (!input.equals("0"));
        }
    }
}
