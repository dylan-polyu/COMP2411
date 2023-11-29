import java.sql.SQLException;
import java.util.Scanner;

public class AdministratorApp {
    Scanner scanner;
    OSS oss;
    public AdministratorApp(OSS oss) throws SQLException {
        scanner = new Scanner(System.in);
        this.oss = oss;
    }
    public void productManagement(){
        while(true){
            System.out.println("\nPlease choose from the following options:");
            System.out.println("1. Add a product");
            System.out.println("2. Remove a product");
            System.out.println("3. Edit a product");
            String input = scanner.next();
            switch(input){
                case "1":

                    break;
                case "2":
                    break;
                case "3":
                    break;
            }
        }
    }
    public void run() throws SQLException {
        System.out.println();
        System.out.println("======================================================");
        System.out.println("Successfully logged into PolyShop Administrator System");
        System.out.println("======================================================");
        boolean running = true;
        while (running) {
            System.out.println("\nPlease choose from the following options:");
            System.out.println("1. Product management");
            System.out.println("2. Inventory management");
            System.out.println("3. Report management");
            System.out.println("4. Display All Products");
            System.out.println("5. Filter Products");
            System.out.println("6. Search Products");
            System.out.println("0. Log Out");
            System.out.print(">> ");
            String input = scanner.nextLine();
            if (input.equals("0")) {
                System.out.println("Thank you for visiting. See you again.");
                running = false;
                break;
            }
            switch (input) {
                case "1":
                    break;
//                    running =
                case "2":
                    break;
                case "3":
                    break;
                case "4":
                    running = oss.displayProduct();
                    break;
                case "5":
                    running = oss.filterProduct();
                    break;
                case "6":
                    running = oss.searchProduct();
                    break;
            }
        }
    }
}
