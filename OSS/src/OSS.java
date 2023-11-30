import java.io.*;
import java.sql.*;
import java.util.Scanner;
import java.util.*;
import oracle.jdbc.driver.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
            if (enteredUserID.equals("0")) {
                return false;
            }
            rset = getStmt(conn).executeQuery("SELECT COUNT(*) FROM USERDATA WHERE userID = '" + enteredUserID + "'");
            getStmt(conn).execute("COMMIT");
            if (rset.next() && rset.getInt(1) >= 1) {
                break;
            }
            System.out.println("\nUsername does not exist! Do you want to make a new account?");
            System.out.print("'y' - creates new account | 'n' - retry\n>> ");
            String input = scanner.next();
            while (!(input.equals("y") || input.equals("n"))) {
                System.out.println("\nInvalid input. Do you want to make a new account?");
                System.out.print("'y' - creates new account | 'n' - retry\n>> ");
                input = scanner.next();
            }
            if (input.equals("y")) {
                System.out.println("\nCreate user or admin account?");
                System.out.print("'u' - user account | 'a' - admin account\n>> ");
                input = scanner.next();
                while (!(input.equals("u") || input.equals("a"))) {
                    System.out.println("\nInvalid input. Do you want to create a user or admin account?");
                    System.out.print("'u' - user account | 'a' - admin account\n>> ");

                    input = scanner.next();
                }
                if (input.equals("u")) {
                    createUserAccount();
                } else {
                    createAdminAccount();
                }
            }
        } while (true);
        int attempts = 2;
        for (; attempts >= 0; attempts--) {
            System.out.print("\nInput password (or input '0' to cancel)\n>> ");
            enteredPassword = scanner.next();
            if (enteredPassword.equals("0")) {
                return false;
            }
            rset = getStmt(conn).executeQuery("SELECT password FROM USERDATA WHERE userID = '" + enteredUserID + "'");
            getStmt(conn).execute("COMMIT");
            if (rset.next()) {
                String storedPassword = rset.getString(1);
                if (!enteredPassword.equals(storedPassword)) {
                    System.out.printf("Incorrect password. Please try again. (You have %d attempts left)%n", attempts);
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
            if (enteredUserID.equals("0")) {
                return false;
            }
            rset = getStmt(conn).executeQuery("SELECT COUNT(*) FROM ADMINISTRATOR WHERE adminID = '" + enteredUserID + "'");
            getStmt(conn).execute("COMMIT");

            if (rset.next() && rset.getInt(1) >= 1) {
                break;
            } else {
                System.out.print("\nUsername does not exist! Do you want to make a new account? (Y to create new account / N to retry)\n>> ");
                String input = scanner.next();
                if (input.equals("Y")) {
                    System.out.print("Create user or admin account? (u for user/ a for admin)\n>> ");
                    input = scanner.next();
                    if (input.equals("u")) {
                        createUserAccount();
                    } else if (input.equals("a")) {
                        createAdminAccount();
                    } else {
                        System.out.println("Invalid input. Redirecting...");
                    }
                }
            }
        } while (true);
        int attempts = 2;
        for (; attempts >= 0; attempts--) {
            System.out.print("\nInput password (or input '0' to cancel)\n>> ");
            enteredPassword = scanner.next();
            if (enteredPassword.equals("0")) {
                return false;
            }
            rset = getStmt(conn).executeQuery("SELECT ADMINPWD FROM ADMINISTRATOR WHERE adminID = '" + enteredUserID + "'");
            getStmt(conn).execute("COMMIT");

            if (rset.next()) {
                String storedPassword = rset.getString(1);
                if (!enteredPassword.equals(storedPassword)) {
                    System.out.printf("Incorrect password. Please try again. (You have %d attempts left)%n", attempts);
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
            if (enteredUserID.equals("0")) {
                return false;
            }
            rset = getStmt(conn).executeQuery("SELECT COUNT(*) FROM USERDATA WHERE userID = '" + enteredUserID + "'");
            getStmt(conn).execute("COMMIT");

            if (rset.next()) {
                if (rset.getInt(1) >= 1) {

                    String input = "";
                    System.out.println("\nUsername already existed. Do you want to login?");
                    input = scanner.next();
                    while (!(input.equals("y") || input.equals("n"))) {
                        System.out.println("\nInvalid input. Do you want to login?");
                        System.out.print("'y' - login | 'n' - cancel\n>> ");
                        input = scanner.next();
                    }
                    if (input.equals("y")) {
                        return loginUserAccount();
                    } else {
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
            if (enteredUserID.equals("0")) {
                return false;
            }
            rset = getStmt(conn).executeQuery("SELECT COUNT(*) FROM ADMINISTRATOR WHERE adminID = '" + enteredUserID + "'");
            getStmt(conn).execute("COMMIT");

            if (rset.next()) {
                if (rset.getInt(1) >= 1) {
                    System.out.println("\nUsername already existed. Do you want to login?");
                    System.out.print("'y' - login | 'n' - cancel\n>> ");
                    String input = scanner.next();
                    if (input.equals("Y")) {
                        return loginAdminAccount();
                    }
                    if (input.equals("N")) {
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

    public void generateReport() throws SQLException{
        System.out.println("\nReport Management System - Select a report type");
        System.out.println("1. Selling report");
        System.out.println("2. Promotion report");
        System.out.println("0. Cancel");
        System.out.print(">> ");
        String input = scanner.next();
        switch(input) {
            case "1" -> {
                System.out.print("\nHere is the selling report based on the purchase history");
                System.out.println("======================================================");
                result = new ArrayList<String>();
                ResultSet rset = getStmt(conn).executeQuery("SELECT * FROM PRODUCT");
                getStmt(conn).execute("COMMIT");

                if (!rset.next()) {
                    System.out.println("no data founded");
                    return;
                }
                int count = 0;
                do {
                    String productName = rset.getString("name");
                    int stockQty = rset.getInt("STOCK_QTY");
                    int unitsSold = rset.getInt("UNITS_SOLD");
                    String productId = rset.getString("productID");
                    result.add(productId);
                    System.out.println();
                    System.out.println(++count + ". Product " + count);
                    System.out.println("Product Name: " + productName);
                    System.out.println("Current stock: " + stockQty);
                    System.out.println("Quantity sold: " + unitsSold);
                } while (rset.next());
            }
            case "2" -> {
                System.out.println("\nPromotion history report");
                System.out.println("======================================================");
                result = new ArrayList<String>();
                ResultSet rset = getStmt(conn).executeQuery("SELECT * FROM PROMOTION t1 JOIN PRODUCT t2 ON t1.PROMOTIONID = t2.PROMOTIONID");
                getStmt(conn).execute("COMMIT");

                if (!rset.next()) {
                    System.out.println("No records found.");
                    return;
                }
                int count = 0;
                do {
                    String PromotionID = rset.getString("PROMOTIONID");
                    String productName = rset.getString("NAME");
                    float discountrate = rset.getFloat("DISCOUNTRATE");
                    String startdate = rset.getString("STARTDATE");
                    String enddate = rset.getString("ENDDATE");
                    int stockQty = rset.getInt("STOCK_QTY");
                    int unitsSold = rset.getInt("UNITS_SOLD");
                    result.add(PromotionID);
                    System.out.println();
                    System.out.println("PromotionID: " + PromotionID);
                    System.out.println("Promoted Product Name: " + productName);
                    System.out.println("Discount rate: " + discountrate*100 + "%");
                    System.out.println("StartDate: " + startdate);
                    System.out.println("EndDate: " + enddate);
                    System.out.println("Current stock of the promoted product: " + stockQty);
                    System.out.println("Quantity sold of that promoted product: " + unitsSold);
                } while (rset.next());
            }
            case "0" -> {
                return;
            }
            default -> {System.out.println("Wrong option, please try again");
                generateReport();}
        }

    }
    List<String> result;
    public boolean searchProduct() throws SQLException {
        result = new ArrayList<String>();
        scanner = new Scanner(System.in);
        System.out.print("\nEnter the keyword to search\n>> ");
        String keyword = scanner.next();
        String query = String.format("SELECT * FROM product WHERE name LIKE '%%%s%%' OR description LIKE '%%%s%%'", keyword, keyword);
        ResultSet rset = getStmt(conn).executeQuery(query);
        if (!rset.next()) {
            System.out.println("Product not found.");
            return false;
        }
        int count = 0;
        System.out.println("Here is the list of product(s):");
        do {
            String productName = rset.getString("name");
            double price = rset.getDouble("price");
            String productId = rset.getString("productID");
            result.add(productId);
            System.out.println("Product #" + ++count);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.printf("Product ID    : %s%n", productId);
            System.out.printf("Product Name  : %s%n", productName);
            System.out.printf("Price         : $%.2f%n", price);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        } while(rset.next());
        return true;
    }
    public boolean filterProduct() throws SQLException {
        ResultSet rset;
        boolean isValidInput = false;
        String input;
        String[] choices;
        do {
            System.out.println("Available Filter Options:");
            System.out.println("1. By Category");
            System.out.println("2. By Brand");
            System.out.println("3. By Price Range");
            System.out.println("0. Cancel");
            System.out.print("Enter the filter options separated by commas (e.g., 1,2,3): ");
            input = scanner.next();
            choices = input.split(",");
            isValidInput = true;
            for (String choice : choices) {
                if (!choice.matches("[0-3]")) {
                    isValidInput = false;
                    break;
                }
            }
            if (!isValidInput) {
                System.out.println("Invalid input. Please enter valid filter options.");
            }
        } while (!isValidInput || !input.equals("0"));
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
                String category = scanner.next();
                query += " category = '" + category + "'";
            }
            if (brandFilter) {
                if (categoryFilter) {
                    query += " AND";
                }
                System.out.print("Enter the brand: ");
                String brand = scanner.next();
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
        getStmt(conn).execute("COMMIT");


        if (!rset.next()) {
            System.out.println("Product not found.");
            return false;
        }
        int count = 0;
        System.out.println("Here is the list of product(s):");
        do {
            String productName = rset.getString("name");
            double price = rset.getDouble("price");
            String productId = rset.getString("productID");
            result.add(productId);
            System.out.println();
            System.out.println("Product #" + ++count);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.printf("Product ID    : %s%n", productId);
            System.out.printf("Product Name  : %s%n", productName);
            System.out.printf("Price         : $%.2f%n", price);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        } while (rset.next());
        return true;
    }
    public boolean displayProduct() throws SQLException {
        result = new ArrayList<String>();
        ResultSet rset = getStmt(conn).executeQuery("SELECT * FROM product");
        getStmt(conn).execute("COMMIT");

        if (!rset.next()) {
            System.out.println("Product not found.");
            return false;
        }
        int count = 0;
        System.out.println("Here is the list of product(s):");
        do {
            String productName = rset.getString("name");
            double price = rset.getDouble("price");
            String productId = rset.getString("productID");
            result.add(productId);

            System.out.println();
            System.out.println("Product #" + ++count);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.printf("Product ID    : %s%n", productId);
            System.out.printf("Product Name  : %s%n", productName);
            System.out.printf("Price         : $%.2f%n", price);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        } while(rset.next());
        return true;
    }
    String productID = "";
    public void productDetails() throws SQLException {
        scanner = new Scanner(System.in);
        String inputStr; int input;
        while (true) {
            System.out.print("\nPlease input the product number (or 0 to cancel):\n>> ");
            inputStr = scanner.next();
            if (inputStr.isEmpty() || !inputStr.matches("\\d+")) {
                System.out.println("Invalid input. Please enter a valid product number.");
                continue;
            }
            input = Integer.parseInt(inputStr);
            if (input == 0) {
                return;
            } else if (input < 1 || input > result.size()) {
                System.out.println("Invalid input. Please enter a valid product number.");
            } else {
                break;
            }
        }
        productID = result.get(input - 1);
        ResultSet rset = getStmt(conn).executeQuery("SELECT * FROM product WHERE productID = '" + productID + "'");
        getStmt(conn).execute("COMMIT");
        if (rset.next()) {
            String productName = rset.getString("name");
            double price = rset.getDouble("price");
            int stock = rset.getInt("stock_qty");
            String description = rset.getString("description");
            String dimensions = rset.getString("dimension");
            String brand = rset.getString("brand");
            String category = rset.getString("category");
            System.out.println();
            System.out.println("Product Details:");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.printf("%-15s: %s%n", "Product Name", productName);
            System.out.printf("%-15s: $%.2f%n", "Price", price);
            System.out.printf("%-15s: %d%n", "Stock", stock);
            System.out.printf("%-15s: %s%n", "Description", description);
            System.out.printf("%-15s: %s%n", "Dimensions", dimensions);
            System.out.printf("%-15s: %s%n", "Brand", brand);
            System.out.printf("%-15s: %s%n", "Category", category);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        }
    }
    public void addToCart() throws SQLException {
        int stock = 0;
        ResultSet rset = getStmt(conn).executeQuery("SELECT STOCK_QTY FROM product WHERE productID = '" + productID + "'");
        getStmt(conn).execute("COMMIT");
        // done
        if(rset.next()) {
            stock = rset.getInt(1);
        }
        if(stock < 1) {
            System.out.println("Product out of stock.");
            return;
        }
        String inputStr; int input = 0;
        do {
            scanner = new Scanner(System.in);
            System.out.print("\nPlease input the amount:\n>> ");
            inputStr = scanner.next();
            if (inputStr.isEmpty() || !inputStr.matches("\\d+")) {
                System.out.println("Invalid input. Please enter a valid amount:");
                inputStr = scanner.next();
                continue;
            }
            input = Integer.parseInt(inputStr);
            if(input > stock) {
                System.out.println("Only " + stock + " item(s) remaining.");
            }
            if(input < 1) {
                System.out.println("Amount has to be at least 1.");
            }
        } while ((inputStr.isEmpty() || !inputStr.matches("\\d+")) || input > stock || input < 1);

        rset = getStmt(conn).executeQuery("SELECT productID, quantity FROM cart WHERE userID = '" + userID + "' AND productID = '" + productID + "'");
        if (rset.next()) {
            getStmt(conn).execute("UPDATE cart SET quantity = " + (input + rset.getInt("quantity")) + " WHERE userID = '" + userID + "' AND productID = '" + productID + "'");
            getStmt(conn).execute ("COMMIT");
            System.out.println("The quantity of the product with ID " + productID + " has been updated in the cart.");
        } else {
            getStmt(conn).execute("INSERT INTO cart (userID, productID, quantity) VALUES ('" + userID + "', '" + productID + "', " + input + ")");

            rset = getStmt(conn).executeQuery("SELECT name FROM product WHERE productID = '" + productID + "'");
            getStmt(conn).execute("COMMIT");
            if(rset.next()) {
                System.out.println("The product '" + rset.getString("name") + "' has been successfully added to cart.");
            }
        }
        getStmt(conn).execute("COMMIT");
    }
    public void removeFromCart() throws SQLException {
        ResultSet rset = getStmt(conn).executeQuery("SELECT name FROM product WHERE productID = '" + productID + "'");
        getStmt(conn).execute("DELETE FROM cart WHERE productID = '" + productID + "' AND userID = '" + userID + "'");
        String checkQuery = "SELECT PRODUCTID FROM orderdetails WHERE PRODUCTID = '" + productID + "'";
        ResultSet checkResult = getStmt(conn).executeQuery(checkQuery);
        getStmt(conn).execute ("COMMIT");

        if (rset.next()) {
            String productName = rset.getString("name");
            if (checkResult.next()) {
                getStmt(conn).execute("DELETE FROM orderdetails WHERE PRODUCTID = '" + productID + "' AND USERID = '" + userID + "'");
                getStmt(conn).execute ("COMMIT");
            }
            System.out.println("The product '" + productName + "' has been successfully removed from cart.");
        }
        getStmt(conn).execute("COMMIT");
    }
    public boolean viewCart() throws SQLException {
        result = new ArrayList<String>();
        String query = "SELECT p.productID, p.name, c.quantity " +
                "FROM cart c " +
                "JOIN product p ON c.productID = p.productID " +
                "WHERE c.userID = '" + userID + "'";
        ResultSet rset = getStmt(conn).executeQuery(query);
        getStmt(conn).execute("COMMIT");
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
            System.out.println("Product " + ++count);
            String productName = rset.getString("name");
            int quantity = rset.getInt("quantity");
            System.out.println("Product ID: " + productID);
            System.out.println("Product Name: " + productName);
            System.out.println("Quantity: " + quantity);
            System.out.println("--------------------");
        } while (rset.next());
        return true;
    }

    public String generateOrderID() throws SQLException {
        String orderID = "O001";
        while (true) {
            ResultSet rset = getStmt(conn).executeQuery("SELECT ORDERID FROM orderdetails WHERE ORDERID = '" + orderID + "'");
            if(rset.next()) {
                int numericPart = Integer.parseInt(orderID.substring(1));
                numericPart++;
                orderID = String.format("O%03d", numericPart);
            } else {
                break;
            }
        }
        return orderID;
    }
    public boolean checkout() throws SQLException {
        if (!viewCart()) {
            System.out.println("Cannot checkout since cart is empty.");
            return false;
        }
        ResultSet rset = getStmt(conn).executeQuery("SELECT p.productID, p.stock_qty, p.name, p.price, c.quantity FROM cart c JOIN product p ON c.productID = p.productID WHERE c.userID = '" + userID + "'");
        getStmt(conn).execute("COMMIT");

        while(rset.next()) {
            String productID = rset.getString("productID");
            int quantity = rset.getInt("quantity");
            int stock = rset.getInt("stock_qty");
            if(stock < quantity) {
                String type = "";
                System.out.print("Only " + stock + " item(s) for " + productID + " remaining. Do you want to proceed? (Y to update quantity / N to cancel)\n>> ");
                while (!type.equals("Y") && !type.equals("N")) {
                    type = scanner.next();
                    if(type.equals("N")) {
                        return false;
                    }
                    else if(type.equals("Y")){
                        break;
                    }
                    System.out.print("Invalid input. Input 'Y' or 'N'.\n>> ");
                }
                quantity = stock;
                getStmt(conn).execute("UPDATE CART SET QUANTITY = " + quantity +" WHERE USERID = '" + userID + "' AND PRODUCTID = '" + productID + "'");
            }
            double price = rset.getDouble("price");
            double totalPrice = price * quantity;
            ResultSet checkResult = getStmt(conn).executeQuery("SELECT PRODUCTID FROM orderdetails WHERE PRODUCTID = '" + productID + "'");
            getStmt(conn).execute("COMMIT");

            if (!checkResult.next()) {
                String orderID = generateOrderID();
                getStmt(conn).execute("INSERT INTO orderdetails (ORDERID, TOTALPRICE, PRODUCTID, USERID) VALUES ('" + orderID + "', " + totalPrice + ", '" + productID + "', '" + userID + "')");
                getStmt(conn).execute("COMMIT");
            }
        }
        getStmt(conn).execute("COMMIT");
        return true;
    }

    public void bill() throws SQLException {
        String input; String paymentMethod = "0";
        do {
            System.out.println("\nHow do you want to pay? ");
            System.out.println("1. Credit Card");
            System.out.println("2. Cash On Delivery");
            System.out.println("0. Back");
            System.out.print(">> ");
            scanner = new Scanner(System.in);
            input = scanner.nextLine();
            if (!(input.equals("1") || input.equals("2") || input.equals("0"))) {
                System.out.println("Invalid input. Please enter 1, 2, or 0.");
            }
        } while (!(input.equals("1") || input.equals("2") || input.equals("0")));
        switch (input) {
            case "0" -> {
                return;
            }
            case "1" -> paymentMethod = "1";
            case "2" -> paymentMethod = "0";
        }
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String billDate = currentDate.format(formatter);
        ResultSet addr = getStmt(conn).executeQuery("SELECT address FROM useraddresses WHERE userId = '" + userID + "'");
        List<String> addresses = new ArrayList<>();
        String selectedAddress = "";
        while(addr.next()) {
            addresses.add(addr.getString("address"));
        }
        if (!addresses.isEmpty()) {
            String inputStr;
            int inputInt;
            System.out.println();
            for (int i = 0; i < addresses.size(); i++) {
                System.out.println((i + 1) + ". " + addresses.get(i));
            }
            while (true) {
                System.out.print("\nPlease input the address (or 0 to cancel):\n>> ");
                inputStr = scanner.nextLine();
                if (inputStr.isEmpty() || !inputStr.matches("\\d+")) {
                    System.out.println("Invalid input. Please enter a valid address number.");
                    continue;
                }
                inputInt = Integer.parseInt(inputStr);
                if (inputInt == 0) {
                    return;
                } else if (inputInt < 1 || inputInt > result.size()) {
                    System.out.println("Invalid input. Please enter a valid address number.");
                } else {
                    break;
                }
            }
            selectedAddress = addresses.get(inputInt - 1);
        }
        ResultSet restock = getStmt(conn).executeQuery("SELECT PRODUCTID, QUANTITY FROM CART WHERE USERID = '" + userID + "'");
        while(restock.next()){
            String productID = restock.getString("PRODUCTID");
            int quantity = restock.getInt("QUANTITY");
            ResultSet test = getStmt(conn).executeQuery("SELECT stock_qty, units_sold FROM product WHERE PRODUCTID = '" + productID + "'");
            int stockQ = 0;
            int units_sold = 0;
            if(test.next()) {
                stockQ = test.getInt("stock_qty") - quantity;
                units_sold = test.getInt("units_sold") + quantity;
            }
            getStmt(conn).execute("UPDATE PRODUCT SET STOCK_QTY = " + stockQ +", UNITS_SOLD = " + units_sold + " WHERE PRODUCTID = '" + productID + "'");
            getStmt(conn).execute("COMMIT");
        }
        ResultSet rset = getStmt(conn).executeQuery("SELECT ORDERID, PRODUCTID TOTALPRICE FROM orderdetails WHERE USERID = '" + userID + "'");
        while (rset.next()) {
            String orderId = rset.getString("ORDERID");
            double totalprice = rset.getDouble("TOTALPRICE");
            getStmt(conn).execute(String.format("INSERT INTO bill (ORDERID, BILLDATE, PAYMENTMETHOD, FINALPRICE, DESTINATION) VALUES ('%s', to_date('%s','YYYY-MM-DD'), '%s', %f, '%s')", orderId, billDate, paymentMethod, totalprice, selectedAddress));
            String productIDTemp = rset.getString("PRODUCTID");
            getStmt(conn).execute("DELETE FROM cart WHERE PRODUCTID = '" + productIDTemp + "' AND userID = '" + userID + "'");
            getStmt(conn).execute("COMMIT");
        }
        System.out.println("Payment successful. Thank you for shopping.");
    }
    public boolean addProduct() throws SQLException {
        String productID, inventoryID, name, brand, category, description, dimension;
        float price, weight;
        int quantity;

        //product ID check
        do {
            System.out.println("Enter product name >> ");
            productID = name = scanner.next();
            if(productID.equals("-1")){
                return false;
            }
            ResultSet rset = getStmt(conn).executeQuery(String.format("SELECT COUNT(*) FROM PRODUCT WHERE productID = '%s'", productID));
            getStmt(conn).execute("COMMIT");
            if (rset.next()) {
                if (rset.getInt(1) == 0) {
                    break;
                }
                System.out.println("Product name exists!");
            }
        } while(true);

        System.out.println("Enter brand >> ");
        brand =  scanner.next();
        if(brand.equals("-1")){
            return false;
        }

        System.out.println("Enter description >> ");
        description =  scanner.next();
        if(description.equals("-1")){
            return false;
        }

        System.out.println("Enter category >> ");
        category =  scanner.next();
        if(category.equals("-1")){
            return false;
        }

        System.out.println("Enter price >> ");
        price =  scanner.nextFloat();
        if(price == -1){
            return false;
        }

        System.out.println("Enter weight >> ");
        weight =  scanner.nextFloat();
        if(weight == -1){
            return false;
        }

        System.out.println("Enter dimension >> ");
        dimension =  scanner.next();
        if(dimension.equals("-1")){
            return false;
        }

        System.out.println("Enter quantity >> ");
        quantity = scanner.nextInt();
        if(quantity == -1){
            return false;
        }

        getStmt(conn).execute(String.format("INSERT INTO PRODUCT (PRODUCTID, NAME, CATEGORY, DESCRIPTION, PRICE, WEIGHT, DIMENSION, BRAND, STOCK_QTY) VALUES ('%s', '%s', '%s','%s', %f, %f, '%s', '%s',%d)",productID, name, category, description, price, weight,dimension,brand,quantity ));
        getStmt(conn).execute("COMMIT");
        return true;
    }

    public boolean getProduct() throws SQLException {
        String inputStr; int input;
        do {
            System.out.println("\nEnter product name\n>> ");
            productID = scanner.next();
            if(productID.equals("-1")){
                return false;
            }
            ResultSet rset = getStmt(conn).executeQuery(String.format("SELECT COUNT(*) FROM PRODUCT WHERE productID = '%s'", productID));
            getStmt(conn).execute("COMMIT");
            if (rset.next()) {
                if (rset.getInt(1) > 0) {
                    break;
                }
                else{
                    System.out.println("Invalid product name.");
                }
            }
        } while(true);

        ResultSet rset = getStmt(conn).executeQuery("SELECT * FROM product WHERE productID = '" + productID + "'");
        getStmt(conn).execute("COMMIT");
        if (rset.next()) {
            String productName = rset.getString("name");
            double price = rset.getDouble("price");
            String description = rset.getString("description");
            String dimensions = rset.getString("dimension");
            String brand = rset.getString("brand");
            String category = rset.getString("category");


            System.out.println();
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.printf("Product Name  : %s%n", productName);
            System.out.printf("Price         : $%.2f%n", price);
            System.out.printf("Description   : %s%n", description);
            System.out.printf("Dimensions    : %s%n", dimensions);
            System.out.printf("Brand         : %s%n", brand);
            System.out.printf("Category      : %s%n", category);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        }
        return true;
    }

    public boolean removeProduct() throws SQLException {
        if(getProduct()){
            System.out.println("To confirm deletion. Re-enter the product name.");
            System.out.print(">>");
            String productName = scanner.next();
            ResultSet rset = getStmt(conn).executeQuery(String.format("SELECT COUNT(*) FROM PRODUCT WHERE PRODUCTID = '%s'",productName));
            getStmt(conn).execute("COMMIT");
            if(rset.next()){
                if(rset.getInt(1)==0){
                    return false;
                }
            }
            getStmt(conn).execute(String.format("DELETE FROM CART WHERE PRODUCTID = '%s'",productName));
            getStmt(conn).execute(String.format("DELETE FROM PRODUCT WHERE PRODUCTID = '%s'",productName));
            getStmt(conn).execute("COMMIT");
            return true;
        }
        return false;
    }

    public boolean editProduct() throws SQLException{
        if(getProduct()){
            System.out.println("1. ProductID");
            System.out.println("2. PromotionID");
            System.out.println("3. Name");
            System.out.println("4. Brand");
            System.out.println("5. Category");
            System.out.println("6. Description");
            System.out.println("7. Price");
            System.out.println("8. DiscountPrice");
            System.out.println("9. Weight");
            System.out.println("10. Dimension");
            System.out.println("11. Stock_Qty");
            System.out.println("12. Units_Sold");
            System.out.println("0. Cancel");
            System.out.println("Select criterion number to change\n>> ");
            int input = scanner.nextInt();

            String criterion = "";
            while(true) {
                boolean flag = false;
                switch (input) {
                    case 1:
                        criterion = "productID";
                        break;
                    case 2:
                        criterion = "PROMOTIONID";
                        break;
                    case 3:
                        criterion = "name";
                        break;
                    case 4:
                        criterion = "brand";
                        break;
                    case 5:
                        criterion = "description";
                        break;
                    case 6:
                        criterion = "category";
                        break;
                    case 7:
                        criterion = "price";
                        break;
                    case 8:
                        criterion = "discountprice";
                        break;
                    case 9:
                        criterion = "weight";
                        break;
                    case 10:
                        criterion = "dimension";
                        break;
                    case 11:
                        criterion = "stock_qty";
                        break;
                    case 12:
                        criterion = "units_sold";
                        break;
                    case 0:
                        return false;
                    default:
                        System.out.println("Invalid input");
                        flag = true;
                        break;
                }
                if(!flag){
                    break;
                }
            }
            System.out.println("To confirm change. Re-enter the product name:\n>> ");
            String productName = scanner.next();
            ResultSet rset = getStmt(conn).executeQuery(String.format("SELECT COUNT(*) FROM PRODUCT WHERE PRODUCTID = '%s'",productName));
            getStmt(conn).execute("COMMIT");
            if(rset.next()){
                if(rset.getInt(1)==0){
                    return false;
                }
            }
            System.out.println("New value:\n>> ");
            if(input == 7 || input == 8 || input == 9 || input == 11 || input == 12){
                getStmt(conn).execute(String.format("UPDATE PRODUCT SET %s = %d WHERE productID = '%s'",criterion,scanner.nextInt(),productName));
                getStmt(conn).execute("COMMIT");
            }
            else{
                getStmt(conn).execute(String.format("UPDATE PRODUCT SET %s = '%s' WHERE productID = '%s'",criterion,scanner.next(),productName));
                getStmt(conn).execute("COMMIT");
            }

            return true;
        }
        return false;
    }

    public void addPromotion() throws SQLException{
        String promotionID, startDate, endDate;
        float discountRate;
        do {
            System.out.println("Enter promotion ID>> ");
            promotionID = scanner.next();
            if(promotionID.equals("-1")){
                return;
            }
            ResultSet rset = getStmt(conn).executeQuery(String.format("SELECT COUNT(*) FROM PROMOTION WHERE PROMOTIONID = '%s'", promotionID));
            getStmt(conn).execute("COMMIT");
            if (rset.next()) {
                if (rset.getInt(1) == 0) {
                    break;
                }
                System.out.println("Promotion name has already existed!");
            }
        } while(true);
        System.out.println("Enter discount rate\n>> ");
        discountRate =  scanner.nextFloat();
        if(discountRate == -1){
            return;
        }
        scanner = new Scanner(System.in);
        System.out.println("Enter start date >> ");
        startDate =  scanner.next();
        if(startDate.equals("-1")){
            return;
        }
        System.out.println("Enter end date >> ");
        endDate =  scanner.next();
        if(endDate.equals("-1")){
            return;
        }
        getStmt(conn).execute(String.format("INSERT INTO PROMOTION (PROMOTIONID, DISCOUNTRATE, STARTDATE, ENDDATE) VALUES ('%s', %f, to_date('%s','YYYY-MM-DD'), to_date('%s','YYYY-MM-DD'))",promotionID, discountRate, startDate, endDate));
        getStmt(conn).execute("COMMIT");
    }

    public Boolean getPromotion() throws SQLException {
        String promotionID;
        do {
            System.out.println("Enter promotion ID >> ");
            promotionID = scanner.next();
            if(promotionID.equals("-1")){
                return false;
            }
            ResultSet rset = getStmt(conn).executeQuery(String.format("SELECT COUNT(*) FROM PROMOTION WHERE PROMOTIONID = '%s'", promotionID));
            getStmt(conn).execute("COMMIT");
            if (rset.next()) {
                if (rset.getInt(1) > 0) {
                    break;
                }
                else{
                    System.out.println("Invalid promotion ID.");
                }
            }
        } while(true);
        ResultSet rset = getStmt(conn).executeQuery(String.format("SELECT * FROM PROMOTION t1 JOIN PRODUCT t2 ON t1.PROMOTIONID = t2.PROMOTIONID WHERE t1.PROMOTIONID = '%s'",promotionID ));
        getStmt(conn).execute("COMMIT");
        if (rset.next()) {
            String PromotionID = rset.getString("PROMOTIONID");
            String productName = rset.getString("NAME");
            float discountrate = rset.getFloat("DISCOUNTRATE");
            String startdate = rset.getString("STARTDATE");
            String enddate = rset.getString("ENDDATE");
            System.out.println();
            System.out.println("PromotionID: " + PromotionID);
            System.out.println("Promoted Product Name: " + productName);
            System.out.println("Discount rate: " + discountrate*100 + "%");
            System.out.println("StartDate: " + startdate);
            System.out.println("EndDate: " + enddate);
        }
        return true;
    }

    public boolean removePromotion() throws SQLException{
        if(getPromotion()){
            System.out.println("To confirm deletion. Re-enter the promotion ID.");
            System.out.print(">>");
            String productName = scanner.next();
            ResultSet rset = getStmt(conn).executeQuery(String.format("SELECT COUNT(*) FROM PRODUCT WHERE PRODUCTID = '%s'",productName));
            getStmt(conn).execute("COMMIT");
            if(rset.next()){
                if(rset.getInt(1)==0){
                    return false;
                }
            }
            getStmt(conn).execute(String.format("DELETE FROM PRODUCT WHERE PRODUCTID = '%s'",productName));
            getStmt(conn).execute("COMMIT");
            return true;
        }
        return false;
    }

    public boolean editPromotion() throws SQLException{
        if(getPromotion()){
            System.out.println("1. DiscountRate");
            System.out.println("2. StartDate");
            System.out.println("3. EndDate");
            System.out.println("0. Cancel");
            System.out.println("Select criterion number to change >> ");
            int input = scanner.nextInt();
            String criterion = "";
            while(true) {
                boolean flag = false;
                switch (input) {
                    case 1:
                        criterion = "DiscountRate";
                        break;
                    case 2:
                        criterion = "StartDate";
                        break;
                    case 3:
                        criterion = "EndDate";
                        break;
                    case 0:
                        return false;
                    default:
                        System.out.println("Invalid input");
                        flag = true;
                        break;
                }
                if(!flag){
                    break;
                }
            }
            System.out.println("To confirm change. Re-enter the promotion ID.");
            System.out.print(">>");
            String promotionID = scanner.next();
            ResultSet rset = getStmt(conn).executeQuery(String.format("SELECT COUNT(*) FROM PROMOTION WHERE PROMOTIONID = '%s'",promotionID));
            getStmt(conn).execute("COMMIT");
            if(rset.next()){
                if(rset.getInt(1)==0){
                    return false;
                }
            }
            System.out.println("New value >> ");
            if(input == 1){
                getStmt(conn).execute(String.format("UPDATE PROMOTION SET %s = %d WHERE PROMOTIONID = '%s'",criterion,scanner.nextInt(),promotionID));
                getStmt(conn).execute("COMMIT");
            }
            else{
                getStmt(conn).execute(String.format("UPDATE PROMOTION SET %s = '%s' WHERE productID = '%s'",criterion,scanner.next(),promotionID));
                getStmt(conn).execute("COMMIT");
            }

            return true;
        }
        return false;
    }
    public boolean printreview() throws SQLException {
        ResultSet rset = getStmt(conn).executeQuery(String.format("SELECT * FROM REVIEW WHERE PRODUCTID = '%s'",productID));
        getStmt(conn).execute("COMMIT");
        int i = 0;
        while(rset.next()) {
            String userId = rset.getString("USERID");
            String comments = rset.getString("COMMENTS");
            float rating = rset.getFloat("RATING");

            // Format the output
            System.out.println("User ID: " + userId);
            System.out.println("Comments: " + comments);
            System.out.println("Rating: " + rating);
            System.out.println("--------------------------");
            i++;
        }
        if(i == 0){
            return false;
        }
        else{
            return true;
        }

    }
    public boolean addReview () throws SQLException {
        String comment = "";
        float rating = 0;
        boolean review = false;
        System.out.println("We treasure all of your responses.");
        System.out.println("Please fill in the form below");
        System.out.println("--------------------------");
        System.out.println("If you want to cancel the process,\nPlease input CANCEL in the input field");
        while (comment.equals("")) {
            System.out.println("What do you feel about this product?");
            scanner.skip("\n");
            comment = scanner.nextLine();
            if (comment.equals("")){System.out.println("Please input a review");}
            else if (comment.equals ("CANCEL")){System.out.println ("Thank You for your time"); return false;}
        }
        while (rating == 0) {
            System.out.println("How much would you rate this product (0.0 - 5.0)");
            String input = scanner.next();
            if (!input.equals("0")){
                rating = Float.parseFloat(input);
            }
            else if(input.equals("CANCEL")){System.out.println ("Thank You for your time"); return false;}
            else{System.out.println ("Please provide a number from 0.0 - 5.0");}
        }
        getStmt(conn).execute(String.format("INSERT INTO REVIEW VALUES ('%s', '%s', '%s', %f)", userID,productID,comment,rating));
        getStmt(conn).execute("COMMIT");
        System.out.println ("Thank You for your time. Your review has been successfully updated");
        return true;
    }
}
