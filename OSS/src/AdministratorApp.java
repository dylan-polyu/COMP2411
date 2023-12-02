import com.sun.net.httpserver.Authenticator;

import java.sql.SQLException;
import java.util.Scanner;

public class AdministratorApp {
    Scanner scanner;
    OSS oss;
    public AdministratorApp(OSS oss) throws SQLException {
        scanner = new Scanner(System.in);
        this.oss = oss;
    }

    private void promotionManagement() throws SQLException{
        while(true){
            System.out.println("\nPlease choose from the following options:");
            System.out.println("1. Add a promotion");
            System.out.println("2. Remove a promotion");
            System.out.println("3. Edit a promotion");
            System.out.println("4. Search a promotion");
            System.out.println("0. Back");
            System.out.print(">> ");
            String input = scanner.next();
            switch(input){
                case "1":
                    if(oss.addPromotion()){
                        System.out.println("Successfully added promotion");
                    }
                    else{
                        System.out.println("Failed to add promotion");
                    }
                    break;
                case "2":
                    if(oss.removePromotion()){
                        System.out.println("Successfully removed promotion");
                    }
                    else{
                        System.out.println("Failed to remove promotion");
                    }
                    break;
                case "3":
                    if(oss.editPromotion()){
                        System.out.println("Successfully edited promotion");
                    }
                    else{
                        System.out.println("Failed to edit promotion");
                    }
                    break;
                case "4":
                    if(oss.getPromotion()){
                        System.out.println("Successfully retrieved promotion");
                    }
                    else{
                        System.out.println("Failed to retrieve promotion");
                    }
                    break;

                case "0":
                    return;
            }
        }
    }
    private void productManagement() throws SQLException {
        while(true){
            System.out.println("\nPlease choose from the following options:");
            System.out.println("1. Add a product");
            System.out.println("2. Remove a product");
            System.out.println("3. Edit a product");
            System.out.println("4. Display All Products");
            System.out.println("5. Filter Products");
            System.out.println("6. Search Products");
            System.out.println("0. Back");
            System.out.print(">> ");
            String input = scanner.next();
            switch(input){
                case "1":
                    if(oss.addProduct()){
                        System.out.println("\nSuccessfully added new product");
                    }
                    else{
                        System.out.println("\nFailed to add product");
                    }
                    break;
                case "2":
                    if(oss.removeProduct()){
                        System.out.println("\nSuccessfully removed product");
                    }
                    else{
                        System.out.println("\nFailed to remove product");
                    }
                    break;
                case "3":
                    if(oss.editProduct()){
                        System.out.println("\nSuccessfully edited product");
                    }
                    else{
                        System.out.println("\nFailed to edit product");
                    }
                    break;
                case "4":
                    oss.displayProduct();
                    break;
                case "5":
                    oss.filterProduct();
                    break;
                case "6":
                    oss.searchProduct();
                    break;
                case "0":
                    return;
            }
        }
    }

    private void transportationManagement() throws SQLException {
        oss.changeTransportationFee();
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
            System.out.println("2. Report management");
            System.out.println("3. Promotion management");
            System.out.println("4. Transportation management");

            System.out.println("0. Log Out");
            System.out.print(">> ");
            String input = scanner.next();
            if (input.equals("0")) {
                System.out.println("Thank you for visiting. See you again.");
//                running = false;
                break;
            }
            switch (input) {
                case "1":
                    productManagement();
                    break;
                case "2":
                    oss.generateReport();
                    break;
                case "3":
                    promotionManagement();
                    break;
                case "4":
                    transportationManagement();
                    break;
            }
        }
    }
}
