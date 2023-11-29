import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) throws SQLException, IOException {
        Scanner scanner = new Scanner(System.in);
        OSS oss = new OSS();
        boolean loginFlag = false;
        while (true) {
            System.out.println("\nPlease choose from the following options:");
            System.out.println("1. Login");
            System.out.println("2. Create Account");
            System.out.println("0. Close App");
            System.out.print(">> ");
            String input = scanner.nextLine();
            if(input.equals("0")) {
                oss.closeApp();
                break;
            }
            switch (input) {
                case "0":
                case "1":
                    loginFlag = oss.loginAccount();
                    break;
                case "2":
                    loginFlag = oss.createAccount();
                    break;
                default:
                    System.out.println("Invalid input.");
                    break;
            }
            if(loginFlag) {
                System.out.println();
                System.out.println("======================================================");
                System.out.println("Welcome to PolyShop, a friendly online shopping system");
                System.out.println("======================================================");
            }
            while(loginFlag){
                System.out.println("\nPlease choose from the following options:");
                System.out.println("1. Display All Products");
                System.out.println("2. Filter Products");
                System.out.println("3. Search Products");
                System.out.println("0. Log Out");
                System.out.print(">> ");
                input = scanner.nextLine();
                if(input.equals("0")) {
                    System.out.println("Thank you for visiting. See you again.");
                    loginFlag = false;
                    break;
                }
                switch (input) {
                    case "1":
                        loginFlag = oss.displayProduct();
                        break;
                    case "2":
                        loginFlag = oss.filterProduct();
                        break;
                    case "3":
                        loginFlag = oss.searchProduct();
                        break;
                }
                while (loginFlag) {
                    System.out.println("\nPlease choose from the following options:");
                    System.out.println("1. Product Details");
                    System.out.println("0. Back");
                    System.out.print(">> ");
                    input = scanner.nextLine();
                    if(input.equals("0")) {
                        break;
                    }
                    switch (input) {
                        case "1":
                            oss.productDetails();
                            break;
                    }
                    while (loginFlag) {
                        System.out.println("\nPlease choose from the following options:");
                        System.out.println("1. Add To Cart");
                        System.out.println("0. Back");
                        System.out.print(">> ");
                        input = scanner.nextLine();
                        if(input.equals("0")) {
                            break;
                        }
                        switch (input) {
                            case "1":
                                oss.addToCart();
                                break;
                        }
                    }
                }
            }
        }

    }
}
