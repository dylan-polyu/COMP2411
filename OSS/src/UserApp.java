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
        System.out.println("======================================================");
        System.out.println("Welcome to PolyShop, a friendly online shopping system");
        System.out.println("======================================================");
        boolean flag = true;
        while(true){
            String input;
            do {
                System.out.println("\nPlease choose from the following options:");
                System.out.println("1. Display All Products");
                System.out.println("2. Filter Products");
                System.out.println("3. Search Products");
                System.out.println("4. View Cart");
                System.out.println("0. Log Out");
                System.out.print(">> ");
                input = scanner.nextLine();
                if (!(input.equals("1") || input.equals("2") || input.equals("3") || input.equals("4") || input.equals("0"))) {
                    System.out.println("Invalid input. Please enter 1, 2, 3, 4, or 0.");
                }
            } while (!(input.equals("1") || input.equals("2") || input.equals("3") || input.equals("4") || input.equals("0")));
            if(input.equals("0")) {
                System.out.println("Thank you for visiting. See you again.");
                break;
            }
            flag = switch (input) {
                case "1" -> oss.displayProduct();
                case "2" -> oss.filterProduct();
                case "3" -> oss.searchProduct();
                case "4" -> oss.viewCart();
                default -> flag;
            };
            String key = input;
            if (flag) {
                do {
                    System.out.println("\nPlease choose from the following options:");
                    System.out.println("1. Product Details");
                    System.out.println("2. Checkout Cart");
                    System.out.println("0. Back");
                    System.out.print(">> ");
                    input = scanner.nextLine();

                    if (!(input.equals("1") || input.equals("2") || input.equals("0"))) {
                        System.out.println("Invalid input. Please enter 1 or 0.");
                    }
                } while (!(input.equals("1") || input.equals("2") || input.equals("0")));
                if (input.equals("2")) {
                    flag = oss.checkout();
                    if(flag) {
                        oss.bill();
                    }
                } else {
                    oss.productDetails();
                    do {
                        System.out.println("\nPlease choose from the following options:");
                        if(key.equals("4")) {
                            System.out.println("1. Remove From Cart");
                        } else {
                            System.out.println("1. Add To Cart");
                        }
                        System.out.println("0. Back");
                        System.out.print(">> ");
                        input = scanner.nextLine();

                        if (!(input.equals("1") || input.equals("0"))) {
                            System.out.println("Invalid input. Please enter 1 or 0.");
                        }
                    } while (!(input.equals("1") || input.equals("0")));
                    if(input.equals("0")) {
                        continue;
                    }
                    if(key.equals("4")) {
                        oss.removeFromCart();
                    } else {
                        oss.addToCart();
                    }
                }
            }
        }
    }
}
