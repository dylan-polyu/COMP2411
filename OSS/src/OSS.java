import java.io.*;
import java.sql.*;
import java.util.Scanner;
import java.util.*;
import oracle.jdbc.driver.*;

public class OSS {
    // Connection
    private static Statement stmt;

    public static Statement getStmt(OracleConnection conn) throws SQLException {
        return conn.createStatement();
    }
    public static void setStmt(Statement stmt) {
        OSS.stmt = stmt;
    }
    OracleConnection conn;
    public Scanner scanner;
    public OSS() throws SQLException {
        scanner = new Scanner(System.in);
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        conn = (OracleConnection) DriverManager.getConnection("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms", "\"22099885d\"", "jnhhpagt");
        setStmt(conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
    }
    public void closeApp() throws SQLException {
        conn.close();
    }
    String userID = "";
    public boolean loginUserAccount() throws SQLException {
        scanner = new Scanner(System.in);
        String enteredUserID, enteredPassword;
        ResultSet rset;
        do {
            System.out.print("\nInput your username (or input '0' to cancel)\n>> ");
            enteredUserID = scanner.next();
            if(enteredUserID.equals("0")) {
                return false;
            }
            rset = getStmt(conn).executeQuery("SELECT COUNT(*) FROM USERDATA WHERE userID = '" + enteredUserID + "'");
            if (rset.next() && rset.getInt(1) >= 1) {
                break;
            } else {
                System.out.print("\nUsername does not exist! Do you want to make a new account? (Y to create new account / N to retry)\n>> ");
                String input = scanner.next();
                if (input.equals("Y")) {
                    System.out.println("Create user or admin account? (u for user/ a for admin) >> ");
                    input = scanner.next();
                    if(input.equals("u")){
                        createUserAccount();
                    }
                    else if(input.equals("a")){
                        createAdminAccount();
                    }
                    else{
                        System.out.println("Invalid input. Redirecting...");
                    }
                }
            }
        } while (true);
        int attempts = 2;
        for (; attempts >= 0; attempts--) {
            System.out.print("\nInput password (or input '0' to cancel)\n>> ");
            enteredPassword = scanner.next();
            if(enteredPassword.equals("0")) {
                return false;
            }
            rset = getStmt(conn).executeQuery("SELECT password FROM USERDATA WHERE userID = '" + enteredUserID + "'");
            if (rset.next()) {
                String storedPassword = rset.getString(1);
                if (!enteredPassword.equals(storedPassword)) {
                    System.out.printf("Incorrect password. Please try again. (You have %d attempts left)%n",attempts);
                } else {
                    break;
                }
            }
        }
        userID = enteredUserID;
        return (attempts >= 0);
    }
    public boolean loginAdminAccount() throws SQLException {
        scanner = new Scanner(System.in);
        String enteredUserID, enteredPassword;
        ResultSet rset;
        do {
            System.out.print("\nInput your username (or input '0' to cancel)\n>> ");
            enteredUserID = scanner.next();
            if(enteredUserID.equals("0")) {
                return false;
            }
            rset = getStmt(conn).executeQuery("SELECT COUNT(*) FROM ADMINISTRATOR WHERE adminID = '" + enteredUserID + "'");
            if (rset.next() && rset.getInt(1) >= 1) {
                break;
            } else {
                System.out.print("\nUsername does not exist! Do you want to make a new account? (Y to create new account / N to retry)\n>> ");
                String input = scanner.next();
                if (input.equals("Y")) {
                    System.out.println("Create user or admin account? (u for user/ a for admin) >> ");
                    input = scanner.next();
                    if(input.equals("u")){
                        createUserAccount();
                    }
                    else if(input.equals("a")){
                        createAdminAccount();
                    }
                    else{
                        System.out.println("Invalid input. Redirecting...");
                    }
                }
            }
        } while (true);
        int attempts = 2;
        for (; attempts >= 0; attempts--) {
            System.out.print("\nInput password (or input '0' to cancel)\n>> ");
            enteredPassword = scanner.next();
            if(enteredPassword.equals("0")) {
                return false;
            }
            rset = getStmt(conn).executeQuery("SELECT ADMINPWD FROM ADMINISTRATOR WHERE adminID = '" + enteredUserID + "'");
            if (rset.next()) {
                String storedPassword = rset.getString(1);
                if (!enteredPassword.equals(storedPassword)) {
                    System.out.printf("Incorrect password. Please try again. (You have %d attempts left)%n",attempts);
                } else {
                    break;
                }
            }
        }
        userID = enteredUserID;
        return (attempts >= 0);
    }
    public boolean createUserAccount() throws SQLException {
        scanner = new Scanner(System.in);
        String enteredUserID, enteredPassword, firstName, lastName, dateOfBirth, email, phoneNumber, address;
        ResultSet rset;
        do {
            boolean checkName = false;
            System.out.print("\nInput unique username (or input '0' to exit)\n>> ");
            enteredUserID = scanner.next();
            if(enteredUserID.equals("0")) {
                return false;
            }
            rset = getStmt(conn).executeQuery("SELECT COUNT(*) FROM USERDATA WHERE userID = '" + enteredUserID + "'");
            if (rset.next()) {
                if (rset.getInt(1) >= 1) {
                    System.out.print("\nUsername already existed. Do you want to login? (Y to login / N to cancel)\n>> ");
                    String input = scanner.next();
                    if(input.equals("Y")) {
                        return loginUserAccount();
                    }
                    if(input.equals("N")) {
                        return false;
                    }
                } else {
                    checkName = true;
                }
            }
            if (checkName) {
                break;
            }
        } while (true);
        System.out.print("First name\n>> ");
        firstName = scanner.next();
        System.out.print("Last name\n>> ");
        lastName = scanner.next();
        System.out.print("Date of Birth\n>> ");
        dateOfBirth = scanner.next();
        System.out.print("Email\n>> ");
        email = scanner.next();
        System.out.print("Address\n>> ");
        address = scanner.next();
        System.out.print("Phone number\n>> ");
        phoneNumber = scanner.next();
        System.out.print("Input password\n>> ");
        enteredPassword = scanner.next();
        getStmt(conn).execute(String.format("INSERT INTO USERDATA VALUES('%s','%s','%s','%s',to_date('%s','YYYY-MM-DD'),'%s','%s')", enteredUserID, firstName, lastName, enteredPassword, dateOfBirth, email, phoneNumber));
        getStmt(conn).execute(String.format("INSERT INTO USERADDRESSES VALUES('%s','%s')", enteredUserID, address));
        getStmt(conn).execute("COMMIT");
        System.out.println("The account '" + enteredUserID + "' has been successfully created.");
        userID = enteredUserID;
        return true;
    }

    public boolean createAdminAccount() throws SQLException {
        scanner = new Scanner(System.in);
        String enteredUserID, enteredPassword, firstName, lastName, dateOfBirth, email, phoneNumber, address;
        ResultSet rset;
        do {
            boolean checkName = false;
            System.out.print("\nInput unique username (or input '0' to exit)\n>> ");
            enteredUserID = scanner.next();
            if(enteredUserID.equals("0")) {
                return false;
            }
            rset = getStmt(conn).executeQuery("SELECT COUNT(*) FROM ADMINISTRATOR WHERE adminID = '" + enteredUserID + "'");
            if (rset.next()) {
                if (rset.getInt(1) >= 1) {
                    System.out.print("\nUsername already existed. Do you want to login? (Y to login / N to cancel)\n>> ");
                    String input = scanner.next();
                    if(input.equals("Y")) {
                        return loginAdminAccount();
                    }
                    if(input.equals("N")) {
                        return false;
                    }
                } else {
                    checkName = true;
                }
            }
            if (checkName) {
                break;
            }
        } while (true);
        System.out.print("First name\n>> ");
        firstName = scanner.next();
        System.out.print("Last name\n>> ");
        lastName = scanner.next();
        System.out.print("Date of Birth\n>> ");
        dateOfBirth = scanner.next();
        System.out.print("Email\n>> ");
        email = scanner.next();
        System.out.print("Address\n>> ");
        address = scanner.next();
        System.out.print("Phone number\n>> ");
        phoneNumber = scanner.next();
        System.out.print("Input password\n>> ");
        enteredPassword = scanner.next();
        getStmt(conn).execute(String.format("INSERT INTO ADMINISTRATOR VALUES('%s','%s','%s','%s',to_date('%s','YYYY-MM-DD'),'%s','%s')", enteredUserID, firstName, lastName, enteredPassword, dateOfBirth, email, phoneNumber));
        getStmt(conn).execute("COMMIT");
        System.out.println("The account '" + enteredUserID + "' has been successfully created.");
        userID = enteredUserID;
        return true;
    }
    List<String> result;
    public boolean searchProduct() throws SQLException {
        result = new ArrayList<String>();
        scanner = new Scanner(System.in);
        System.out.print("\nEnter the keyword to search: ");
        String keyword = scanner.next();
        String query = String.format("SELECT * FROM product WHERE name LIKE '%%%s%%' OR description LIKE '%%%s%%'", keyword, keyword);        ResultSet rset = getStmt(conn).executeQuery(query);
        if (!rset.next()) {
            System.out.println("Product not found.");
            return false;
        }
        int count = 0;
        do {
            String productName = rset.getString("name");
            double price = rset.getDouble("price");

            String productId = rset.getString("productID");
            result.add(productId);
            System.out.println();
            System.out.println(++count + ". Product " + count);
            System.out.println("Product Name: " + productName);
            System.out.println("Price: $" + price);
        } while (rset.next());
        return true;
    }
    public boolean filterProduct() throws SQLException {
        result = new ArrayList<String>();
        scanner = new Scanner(System.in);
        ResultSet rset;
        System.out.println("Available Filter Options:");
        System.out.println("1. By Category");
        System.out.println("2. By Brand");
        System.out.println("3. By Price Range");
        System.out.println("0. Cancel");
        System.out.print("Enter the filter options separated by commas (e.g., 1,2,3): ");
        String input = scanner.nextLine();
        String[] choices = input.split(",");
        boolean categoryFilter = false, brandFilter = false, priceRangeFilter = false;
        for (String choice : choices) {
            int option = Integer.parseInt(choice.trim());
            switch (option) {
                case 1:
                    categoryFilter = true;
                    break;
                case 2:
                    brandFilter = true;
                    break;
                case 3:
                    priceRangeFilter = true;
                    break;
                default:
                    System.out.println("Invalid filter option: " + option);
                    break;
            }
        }
        String query = "SELECT * FROM product";
        if (categoryFilter || brandFilter || priceRangeFilter) {
            query += " WHERE";
            if (categoryFilter) {
                System.out.print("Enter the category: ");
                String category = scanner.nextLine();
                query += " category = '" + category + "'";
            }
            if (brandFilter) {
                if (categoryFilter) {
                    query += " AND";
                }
                System.out.print("Enter the brand: ");
                String brand = scanner.nextLine();
                query += " brand = '" + brand + "'";
            }
            if (priceRangeFilter) {
                if (categoryFilter || brandFilter) {
                    query += " AND";
                }
                System.out.print("Enter the minimum price: ");
                double minPrice = scanner.nextDouble();
                System.out.print("Enter the maximum price: ");
                double maxPrice = scanner.nextDouble();
                query += " price BETWEEN " + minPrice + " AND " + maxPrice;
            }
        }
        rset = getStmt(conn).executeQuery(query);

        if (!rset.next()) {
            System.out.println("Product not found.");
            return false;
        }
        int count = 0;
        do {
            String productName = rset.getString("name");
            double price = rset.getDouble("price");
            String productId = rset.getString("productID");
            result.add(productId);
            System.out.println();
            System.out.println(++count + ". Product " + count);
            System.out.println("Product Name: " + productName);
            System.out.println("Price: $" + price);
        } while (rset.next());
        return true;
    }
    public boolean displayProduct() throws SQLException {
        result = new ArrayList<String>();
        ResultSet rset = getStmt(conn).executeQuery("SELECT * FROM product");
        if (!rset.next()) {
            System.out.println("Product not found.");
            return false;
        }
        int count = 0;
        do {
            String productName = rset.getString("name");
            double price = rset.getDouble("price");
            String productId = rset.getString("productID");
            result.add(productId);
            System.out.println();
            System.out.println(++count + ". Product " + count);
            System.out.println("Product Name: " + productName);
            System.out.println("Price: $" + price);
        } while (rset.next());
        return true;
    }
    String productID = "";
    public void productDetails() throws SQLException {
        scanner = new Scanner(System.in);
        System.out.print("\nPlease input the product number:\n>> ");
        String inputStr = scanner.nextLine();
        if (inputStr.isEmpty() || !inputStr.matches("\\d+")) {
            System.out.println("Invalid input. Please enter a valid product number.");
            return;
        }
        int input = Integer.parseInt(inputStr);
        if(input < 1 || input > result.size()) {
            System.out.println("Invalid input. Please enter a valid product number.");
            return;
        }
        productID = result.get(input - 1);
        ResultSet rset = getStmt(conn).executeQuery("SELECT * FROM product WHERE productID = '" + productID + "'");
        if (rset.next()) {
            String productName = rset.getString("name");
            double price = rset.getDouble("price");
            String description = rset.getString("description");
            String dimensions = rset.getString("dimension");
            String brand = rset.getString("brand");
            String category = rset.getString("category");
            System.out.println();
            System.out.println("Product Name: " + productName);
            System.out.println("Price: $" + price);
            System.out.println("Description: " + description);
            System.out.println("Dimensions: " + dimensions);
            System.out.println("Brand: " + brand);
            System.out.println("Category: " + category);
        }
    }
    static String orderID = "O001";
    public void addToCart() throws SQLException {
        scanner = new Scanner(System.in);
        System.out.print("\nPlease input the amount:\n>> ");
        String inputStr = scanner.nextLine();
        while (inputStr.isEmpty() || !inputStr.matches("\\d+")) {
            System.out.println("Invalid input. Please enter a valid amount:");
            inputStr = scanner.nextLine();
        }
        int input = Integer.parseInt(inputStr);
        ResultSet rset = getStmt(conn).executeQuery("SELECT productID, quantity FROM cart WHERE userID = '" + userID + "' AND productID = '" + productID + "'");
        if (rset.next()) {
            int existingQuantity = rset.getInt("quantity");
            int newQuantity = existingQuantity + input;
            getStmt(conn).execute("UPDATE cart SET quantity = " + (input + rset.getInt("quantity")) + " WHERE userID = '" + userID + "' AND productID = '" + productID + "'");
            System.out.println("The quantity of the product with ID " + productID + " has been updated in the cart.");
        } else {
            getStmt(conn).execute("INSERT INTO cart (userID, orderID, productID, quantity) VALUES ('" + userID + "', '" + orderID + "', '" + productID + "', " + input + ")");

            rset = getStmt(conn).executeQuery("SELECT name FROM product WHERE productID = '" + productID + "'");
            if(rset.next()) {
                System.out.println("The product '" + rset.getString("name") + "' has been successfully added to cart.");
            }

            int numericPart = Integer.parseInt(orderID.substring(1));
            numericPart++;
            orderID = String.format("O%03d", numericPart);
        }
    }
    public void removeFromCart() throws SQLException {
        ResultSet rset = getStmt(conn).executeQuery("SELECT name FROM product WHERE productID = '" + productID + "'");
        getStmt(conn).execute("DELETE FROM cart WHERE productID = '" + productID + "' AND userID = '" + userID + "'");
        if(rset.next()) {
            System.out.println("The product '" + rset.getString("name") + "' has been successfully deleted from cart.");
        }
    }
    public boolean viewCart() throws SQLException {
        result = new ArrayList<String>();
        String query = "SELECT p.productID, p.name, c.quantity " +
                "FROM cart c " +
                "JOIN product p ON c.productID = p.productID " +
                "WHERE c.userID = '" + userID + "'";
        ResultSet rset = getStmt(conn).executeQuery(query);
        if(!rset.next()) {
            System.out.println("\nCart is currently empty.");
            return false;
        }
        int count = 0;
        System.out.println("\nProducts in Cart:\n--------------------");
        result = new ArrayList<String>();
        do {
            String productID = rset.getString("productID");
            result.add(productID);
            System.out.println(++count + ". Product " + count);
            String productName = rset.getString("name");
            int quantity = rset.getInt("quantity");
            System.out.println("Product ID: " + productID);
            System.out.println("Product Name: " + productName);
            System.out.println("Quantity: " + quantity);
            System.out.println("--------------------");
        } while (rset.next());
        return true;
    }
}