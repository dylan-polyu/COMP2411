import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) throws SQLException, IOException {
        Scanner scanner = new Scanner(System.in);
        OSS oss = new OSS();
        boolean flag = false;
        while (true) {
            String input;
            do {
                System.out.println("\nPlease choose from the following options:");
                System.out.println("1. Login");
                System.out.println("2. Create Account");
                System.out.println("0. Close App");
                System.out.print(">> ");
                input = scanner.nextLine();
                if (!(input.equals("1") || input.equals("2") || input.equals("0"))) {
                    System.out.println("Invalid input. Please enter 1, 2, or 0.");
                }
            } while (!(input.equals("1") || input.equals("2") || input.equals("0")));
            if(input.equals("0")) {
                oss.closeApp();
                break;
            }
            switch (input) {
                case "1" -> flag = oss.loginAccount();
                case "2" -> flag = oss.createAccount();
                default -> System.out.println("Invalid input.");
            }

            if(flag) {
                System.out.println();
                System.out.println("======================================================");
                System.out.println("Welcome to PolyShop, a friendly online shopping system");
                System.out.println("======================================================");
            }
            while(flag){
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
                    flag = false;
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
                        System.out.println("0. Back");
                        System.out.print(">> ");
                        input = scanner.nextLine();

                        if (!(input.equals("1") || input.equals("0"))) {
                            System.out.println("Invalid input. Please enter 1 or 0.");
                        }
                    } while (!(input.equals("1") || input.equals("0")));
                    if(input.equals("0")) {
                        break;
                    }
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
                        break;
                    }
                    if(key.equals("4")) {
                        oss.addToCart();
                    } else {
                        oss.removeFromCart();
                    }
                }
            }
        }
    }
}