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
    public boolean loginUser() throws SQLException {
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
                    createUser();
                } else {
                    createAdmin();
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
    public boolean loginAdmin() throws SQLException {
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
                System.out.println("\nUsername does not exist! Do you want to make a new account?");
                System.out.print("'y' - new account | 'n' - retry\n>> ");
                String input = scanner.next();
                if (input.equals("y")) {
                    System.out.print("\nCreate user or admin account?");
                    System.out.print("'u' - user account | 'a' - admin account\n>> ");
                    input = scanner.next();
                    if (input.equals("u")) {
                        createUser();
                    } else if (input.equals("a")) {
                        createAdmin();
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
    public boolean createUser() throws SQLException {
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
                        return loginUser();
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
    public boolean createAdmin() throws SQLException {
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
                        return loginAdmin();
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
        System.out.println("\nThe account '" + enteredUserID + "' has been successfully created.");
        userID = enteredUserID;
        return true;
    }
    public void generateReport() throws SQLException{
        System.out.println("\nReport Management System - Select a report type");
        System.out.println("1. Sales report");
        System.out.println("2. Promotion report");
        System.out.println("0. Cancel");
        System.out.print(">> ");
        String input = scanner.next();
        switch(input) {
            case "1" -> { // selling report
                System.out.println("\nPlease select your preferred order:");
                System.out.println("1. Descending order by views time");
                System.out.println("2. Ascending order by views time");
                System.out.println("3. Descending order by Sales volume");
                System.out.println("4. Ascending order by Sales volume");
                System.out.print(">> ");
                int optionChosen = scanner.nextInt();
                ResultSet rset = null;
                switch (optionChosen) {
                    case 1 ->{
                        System.out.print("\nHere is the sales report based on the purchase history (Descending order by views time)");
                        System.out.println("\n---------------------------------------------------------------------------");
                        result = new ArrayList<String>();
                        rset = getStmt(conn).executeQuery("SELECT * FROM PRODUCT ORDER BY VIEWS DESC");
                        getStmt(conn).execute("COMMIT");
                    }
                    case 2 -> {
                        System.out.print("\nHere is the sales report based on the purchase history (Ascending order by views time)");
                        System.out.println("\n---------------------------------------------------------------------------");
                        result = new ArrayList<String>();
                        rset = getStmt(conn).executeQuery("SELECT * FROM PRODUCT ORDER BY VIEWS ASC");
                        getStmt(conn).execute("COMMIT");
                    }
                    case 3 ->{
                        System.out.print("\nHere is the sales report based on the purchase history (Descending order by Sales volume)");
                        System.out.println("\n---------------------------------------------------------------------------");
                        result = new ArrayList<String>();
                        rset = getStmt(conn).executeQuery("SELECT * FROM PRODUCT ORDER BY UNITS_SOLD DESC");
                        getStmt(conn).execute("COMMIT");
                    }
                    case 4 ->{
                        System.out.print("\nHere is the sales report based on the purchase history (Ascending order by Sales volume)");
                        System.out.println("\n---------------------------------------------------------------------------");
                        result = new ArrayList<String>();
                        rset = getStmt(conn).executeQuery("SELECT * FROM PRODUCT ORDER BY UNITS_SOLD ASC");
                        getStmt(conn).execute("COMMIT");
                    }
                    default -> {
                        System.out.println("\nWrong option, please choose again.");
                        generateReport();
                    }
                }
                assert rset != null;
                if (!rset.next()) {
                    System.out.println("\nNo data found");
                    return;
                }
                int count = 0;
                do {
                    String productName = rset.getString("name");
                    int stockQty = rset.getInt("STOCK_QTY");
                    int unitsSold = rset.getInt("UNITS_SOLD");
                    int price = rset.getInt("PRICE");
                    int view = rset.getInt("VIEWS");
                    String productId = rset.getString("productID");
                    result.add(productId);
                    System.out.println();
                    System.out.println(++count + ". Product " +productName);
                    System.out.println("Product Name: " + productName);
                    System.out.println("Current stock: " + stockQty);
                    System.out.println("Sales volume: " + unitsSold);
                    System.out.println("Sales: " + price*unitsSold);
                    System.out.println("Views: " + view );
                } while (rset.next());
            }
            case "2" -> {
                System.out.println("\nPromotion history report");
                System.out.println("======================================================");
                result = new ArrayList<String>();
                ResultSet rset = getStmt(conn).executeQuery("SELECT * FROM PROMOTION t1 JOIN PRODUCT t2 ON t1.PROMOTIONID = t2.PROMOTIONID");
                getStmt(conn).execute("COMMIT");

                if (!rset.next()) {
                    System.out.println("\nNo records found.");
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
            default -> {System.out.println("\nWrong option, please try again");
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
            System.out.println("\nProduct not found.");
            return false;
        }
        int count = 0;
        System.out.println("\nHere is the list of product(s)\n");
        do {
            String productName = rset.getString("name");
            double price = rset.getDouble("price");
            double discPrice = rset.getDouble("discountprice");
            String productId = rset.getString("productID");
            result.add(productId);
            System.out.println("Product #" + ++count);
            if(discPrice != 0) {
                System.out.println("This product has a discount!");
            }
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.printf("Product ID    : %s%n", productId);
            System.out.printf("Product Name  : %s%n", productName);
            System.out.printf("Price         : $%.2f%n", price);
            if(discPrice != 0) {
                System.out.printf("Discounted    : $%.2f%n", discPrice);
            }
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        } while(rset.next());
        return true;
    }
    public boolean filterProduct() throws SQLException {
        ResultSet rset;
        boolean categoryFilter = false, brandFilter = false, priceRangeFilter = false;
        String input = "";
        System.out.println("\nAvailable Filter Options:");
        System.out.println("1. By Category");
        System.out.println("2. By Brand");
        System.out.println("3. By Price Range");
        do {
            System.out.println("\nFilter by Category? (or input '0' to cancel)");
            System.out.print("'y' - yes | 'n' - no | '0' - back\n>> ");
            input = scanner.next();
            if(input.equals("0")) { return false; }
        } while (!(input.equals("y") || input.equals("n") || input.equals("0")));
        if (input.equals("y")) {
            categoryFilter = true;
        }
        do {
            System.out.println("\nFilter by Brand? (or input '0' to cancel)");
            System.out.print("'y' - yes | 'n' - no | '0' - back\n>> ");
            input = scanner.next();
            if(input.equals("0")) { return false; }
        } while (!(input.equals("y") || input.equals("n") || input.equals("0")));
        if (input.equals("y")) {
            brandFilter = true;
        }
        do {
            System.out.println("\nFilter by Price? (or input '0' to cancel)");
            System.out.print("'y' - yes | 'n' - no | '0' - back\n>> ");
            input = scanner.next();
            if(input.equals("0")) { return false; }
        } while (!(input.equals("y") || input.equals("n")));
        if (input.equals("y")) {
            priceRangeFilter = true;
        }
        String query = "SELECT * FROM product";
        if (categoryFilter || brandFilter || priceRangeFilter) {
            query += " WHERE";
            if (categoryFilter) {
                System.out.print("\nEnter the category (or input '0' to cancel)\n>> ");
                String category = scanner.next();
                query += " category = '" + category + "'";
            }
            if (brandFilter) {
                if (categoryFilter) {
                    query += " AND";
                }
                System.out.print("\nEnter the brand (or input '0' to cancel)\n>> ");
                String brand = scanner.next();
                query += " brand = '" + brand + "'";
            }
            if (priceRangeFilter) {
                if (categoryFilter || brandFilter) {
                    query += " AND";
                }
                double minPrice = 0.0;
                double maxPrice = 0.0;
                boolean isValidInput;

                do {
                    isValidInput = true;
                    System.out.print("\nEnter the minimum price\n>> ");
                    if (scanner.hasNextDouble()) {
                        minPrice = scanner.nextDouble();
                        System.out.print("\nEnter the maximum price\n>> ");
                        if (scanner.hasNextDouble()) {
                            maxPrice = scanner.nextDouble();
                            if (minPrice > maxPrice) {
                                System.out.println("\nInvalid input. The minimum price must be less than or equal to the maximum price.");
                                isValidInput = false;
                            }
                        } else {
                            System.out.println("\nInvalid input. Please enter a valid maximum price.");
                            scanner.next();
                            isValidInput = false;
                        }
                    } else {
                        System.out.println("\nInvalid input. Please enter a valid minimum price.");
                        scanner.next();
                        isValidInput = false;
                    }
                } while (!isValidInput);

                query += " price BETWEEN " + minPrice + " AND " + maxPrice;
            }
        } else {
            return false;
        }
        rset = getStmt(conn).executeQuery(query);
        getStmt(conn).execute("COMMIT");
        if (!rset.next()) {
            System.out.println("\nProduct not found.");
            return false;
        }
        int count = 0;
        System.out.println("\nHere is the list of product(s):");
        do {
            String productName = rset.getString("name");
            double price = rset.getDouble("price");
            double discPrice = rset.getDouble("discountprice");
            String productId = rset.getString("productID");
            result.add(productId);
            System.out.println();
            System.out.println("Product #" + ++count);
            if(discPrice != 0) {
                System.out.println("This product has a discount!");
            }
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.printf("Product ID    : %s%n", productId);
            System.out.printf("Product Name  : %s%n", productName);
            System.out.printf("Price         : $%.2f%n", price);
            if(discPrice != 0) {
                System.out.printf("Discounted    : $%.2f%n", discPrice);
            }
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        } while (rset.next());
        return true;
    }
    public boolean displayProduct() throws SQLException {
        result = new ArrayList<String>();
        ResultSet rset = getStmt(conn).executeQuery("SELECT * FROM product");
        getStmt(conn).execute("COMMIT");

        if (!rset.next()) {
            System.out.println("\nProduct not found.");
            return false;
        }
        int count = 0;
        System.out.println("\nHere is the list of product(s)");
        do {
            String productName = rset.getString("name");
            double price = rset.getDouble("price");
            double discPrice = rset.getDouble("discountprice");
            String productId = rset.getString("productID");
            result.add(productId);

            System.out.println();
            System.out.println("Product #" + ++count);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            if(discPrice != 0) {
                System.out.println("This product has a discount!");
            }
            System.out.printf("Product ID    : %s%n", productId);
            System.out.printf("Product Name  : %s%n", productName);
            System.out.printf("Price         : $%.2f%n", price);
            if(discPrice != 0) {
                System.out.printf("Discounted    : $%.2f%n", discPrice);
            }
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        } while(rset.next());
        return true;
    }
    String productID = "";
    public boolean productDetails() throws SQLException {
        scanner = new Scanner(System.in);
        String inputStr; int input;
        while (true) {
            System.out.print("\nPlease input the product number (or 0 to cancel):\n>> ");
            inputStr = scanner.next();
            if (inputStr.isEmpty() || !inputStr.matches("\\d+")) {
                System.out.println("\nInvalid input. Please enter a valid product number.");
                continue;
            }
            input = Integer.parseInt(inputStr);
            if (input == 0) {
                return false;
            } else if (input < 1 || input > result.size()) {
                System.out.println("\nInvalid input. Please enter a valid product number.");
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
            double discPrice = rset.getDouble("discountprice");
            int stock = rset.getInt("stock_qty");
            String description = rset.getString("description");
            String dimensions = rset.getString("dimension");
            String brand = rset.getString("brand");
            String category = rset.getString("category");
            int view = rset.getInt("views");
            System.out.println();
            System.out.println("Product Details");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            if(discPrice != 0) {
                System.out.println("This product has a discount!");
            }
            System.out.printf("%-15s: %s%n", "Product Name", productName);
            System.out.printf("%-15s: $%.2f%n", "Price", price);
            if(discPrice != 0) {
                System.out.printf("%-15s: $%.2f%n", "Discounted", discPrice);
            }
            System.out.printf("%-15s: %d%n", "Stock", stock);
            System.out.printf("%-15s: %s%n", "Description", description);
            System.out.printf("%-15s: %s%n", "Dimensions", dimensions);
            System.out.printf("%-15s: %s%n", "Brand", brand);
            System.out.printf("%-15s: %s%n", "Category", category);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            getStmt(conn).execute("UPDATE PRODUCT SET VIEWS = " + ((int)view+1)+ " WHERE PRODUCTID = '" + productID + "'");
        }
        return true;
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
            System.out.println("\nProduct out of stock.");
            return;
        }
        String inputStr; int input = 0;
        do {
            scanner = new Scanner(System.in);
            System.out.print("\nPlease input the amount:\n>> ");
            inputStr = scanner.next();
            if (inputStr.isEmpty() || !inputStr.matches("\\d+")) {
                System.out.println("\nInvalid input. Please enter a valid amount:");
                inputStr = scanner.next();
                continue;
            }
            input = Integer.parseInt(inputStr);
            if(input > stock) {
                System.out.println("\nOnly " + stock + " item(s) remaining.");
            }
            if(input < 1) {
                System.out.println("\nAmount has to be at least 1.");
            }
        } while ((inputStr.isEmpty() || !inputStr.matches("\\d+")) || input > stock || input < 1);

        rset = getStmt(conn).executeQuery("SELECT productID, quantity FROM cart WHERE userID = '" + userID + "' AND productID = '" + productID + "'");
        if (rset.next()) {
            getStmt(conn).execute("UPDATE cart SET quantity = " + (input + rset.getInt("quantity")) + " WHERE userID = '" + userID + "' AND productID = '" + productID + "'");
            getStmt(conn).execute ("COMMIT");
            System.out.println("\nThe quantity of the product with ID '" + productID + "' has been updated in the cart.");
        } else {
            getStmt(conn).execute("INSERT INTO cart (userID, productID, quantity) VALUES ('" + userID + "', '" + productID + "', " + input + ")");

            rset = getStmt(conn).executeQuery("SELECT name FROM product WHERE productID = '" + productID + "'");
            getStmt(conn).execute("COMMIT");
            if(rset.next()) {
                System.out.println("\nThe product '" + rset.getString("name") + "' has been successfully added to cart.");
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
            System.out.println("\nThe product '" + productName + "' has been successfully removed from cart.");
        }
        getStmt(conn).execute("COMMIT");
    }
    public boolean viewCart() throws SQLException {
        result = new ArrayList<String>();
        String query = "SELECT p.productID, p.name, p.price, p.discountprice, c.quantity " +
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
        System.out.println("\nProduct(s) in Cart");
        result = new ArrayList<String>();
        do {
            String productID = rset.getString("productID");
            result.add(productID);

            String productName = rset.getString("name");
            int quantity = rset.getInt("quantity");
            double price = rset.getDouble("price");
            double discPrice = rset.getDouble("discountprice");
            System.out.println();
            System.out.println("Product #" + ++count);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.printf("%-15s: %s%n", "Product ID", productID);
            System.out.printf("%-15s: %s%n", "Product Name", productName);
            System.out.printf("%-15s: %d%n", "Quantity", quantity);
            System.out.printf("%-15s: $%.2f%n", "Price", price);
            if(discPrice != 0) {
                System.out.printf("%-15s: $%.2f%n", "Discounted", discPrice);
                System.out.printf("%-15s: $%.2f%n", "Total Price", discPrice * quantity);
            } else {
                System.out.printf("%-15s: $%.2f%n", "Total Price", price * quantity);
            }
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        } while (rset.next());

        return true;
    }
    public String generateOrderID() throws SQLException {
        ResultSet rset = getStmt(conn).executeQuery("SELECT MAX(ORDERID) AS MAX_ORDERID FROM orderdetails");
        String orderID = "O001";
        if (rset.next()) {
            String maxOrderID = rset.getString("MAX_ORDERID");
            if (maxOrderID != null) {
                int numericPart = Integer.parseInt(maxOrderID.substring(1));
                numericPart++;
                orderID = String.format("O%03d", numericPart);
            }
        }

        return orderID;
    }
    public boolean checkout() throws SQLException {
        if (!viewCart()) {
            System.out.println("\nCannot checkout since cart is empty.");
            return false;
        }
        ResultSet rset = getStmt(conn).executeQuery("SELECT p.productID, p.stock_qty, p.name, p.price,p.discountprice, c.quantity FROM cart c JOIN product p ON c.productID = p.productID WHERE c.userID = '" + userID + "'");
        getStmt(conn).execute("COMMIT");

        while(rset.next()) {
            String productID = rset.getString("productID");
            int quantity = rset.getInt("quantity");
            int stock = rset.getInt("stock_qty");
            if(stock < quantity) {
                String type = "";
                System.out.print("\nOnly " + stock + " item(s) for " + productID + " remaining. Do you want to proceed?\n");
                System.out.print("'y' - creates new account | 'n' - retry\n>> ");
                while (true) {
                    type = scanner.next();
                    if(type.equals("n")) {
                        return false;
                    }
                    else if(type.equals("y")){
                        break;
                    }
                    System.out.print("\nInvalid input. Input 'y' or 'n'.\n>> ");
                }
                quantity = stock;
                getStmt(conn).execute("UPDATE CART SET QUANTITY = " + quantity +" WHERE USERID = '" + userID + "' AND PRODUCTID = '" + productID + "'");
            }
            double discountprice = rset.getDouble("discountprice");
            double price = rset.getDouble("price");
            if(discountprice != 0){
                price = discountprice;
            }
            double totalPrice = price * quantity;
            getStmt(conn).execute("COMMIT");
            String input;
            do {
                System.out.println("\nChoose your delivery option ");
                System.out.println("1. Basic");
                System.out.println("2. Express");
                System.out.println("0. Back");
                System.out.print(">> ");
                scanner = new Scanner(System.in);
                input = scanner.nextLine();
                if (!(input.equals("1") || input.equals("2") || input.equals("0"))) {
                    System.out.println("\nInvalid input. Please enter 1, 2, or 0.");
                }
            } while (!(input.equals("1") || input.equals("2") || input.equals("0")));
            String transport = "";
            switch (input) {
                case "0" -> {
                    return false;
                }
                case "1" -> {
                    transport = "B";
                }
                case "2" -> {
                    transport = "E";
                }
            }
            String orderID = generateOrderID();
            getStmt(conn).execute("INSERT INTO orderdetails (ORDERID, TOTALPRICE, PRODUCTID, TRANSPORTID, USERID) VALUES ('" + orderID + "', " + totalPrice + ", '" + productID + "', '" + transport + "', '" + userID + "')");
            getStmt(conn).execute("COMMIT");

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
                System.out.println("\nInvalid input. Please enter 1, 2, or 0.");
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
        int count = 0;
        String inputStr; int inputInt;
        while(addr.next()) {
            addresses.add(addr.getString("address"));
        }
        if (!addresses.isEmpty()) {
            System.out.println();
            System.out.println("Addresses:");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            for (String address : addresses) {
                System.out.printf("%d. %s%n", ++count, address);
            }
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            scanner = new Scanner(System.in);
            while (true) {
                System.out.print("\nPlease input the address number (or 0 to cancel):\n>> ");
                inputStr = scanner.next();
                if (inputStr.isEmpty() || !inputStr.matches("\\d+")) {
                    System.out.println("\nInvalid input. Please enter a valid address number.");
                    continue;
                }
                inputInt = Integer.parseInt(inputStr);
                if (inputInt == 0) {
                    return;
                } else if (inputInt < 1 || inputInt > addresses.size()) {
                    System.out.println("\nInvalid input. Please enter a valid address number.");
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
        ResultSet rset = getStmt(conn).executeQuery("SELECT * FROM ORDERDETAILS WHERE ORDERID NOT IN (SELECT ORDERID FROM BILL) AND USERID = '" + userID + "'");
        List<String> orderIDs = new ArrayList<String>();
        while (rset.next()) {
            String prodID = rset.getString("PRODUCTID");
            String orderId = rset.getString("ORDERID");
            orderIDs.add(orderId);
            double totalprice = rset.getDouble("TOTALPRICE");
            getStmt(conn).execute(String.format("INSERT INTO bill (ORDERID, BILLDATE, PAYMENTMETHOD, FINALPRICE, DESTINATION) VALUES ('%s', to_date('%s','YYYY-MM-DD'), '%s', %f, '%s')", orderId, billDate, paymentMethod, totalprice, selectedAddress));
            getStmt(conn).execute("DELETE FROM cart WHERE PRODUCTID = '" + prodID + "' AND userID = '" + userID + "'");
            getStmt(conn).execute("COMMIT");
        }
        System.out.println("\nPayment successful. Here is your bill:");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        for (String id : orderIDs) {
            ResultSet billSet = getStmt(conn).executeQuery("SELECT * FROM orderdetails WHERE ORDERID = '" + id + "'");
            if (billSet.next()) {
                String orderID = billSet.getString("orderID");
                String tid = billSet.getString("transportid");
                ResultSet tSet = getStmt(conn).executeQuery("SELECT COST FROM TRANSPORT WHERE TRANSPORTID = '" + tid + "'");
                float tcost = 0;
                if(tSet.next()) {
                    tcost = tSet.getFloat("COST");
                }
                float totalPrice = billSet.getFloat("TOTALPRICE") + tcost;
                System.out.printf("Order ID: %s%n", orderID);

                ResultSet prodSet = getStmt(conn).executeQuery("SELECT name FROM PRODUCT WHERE PRODUCTID = '" + billSet.getString("PRODUCTID") + "'");
                if (prodSet.next()) {
                    String productName = prodSet.getString("name");
                    System.out.printf("Product Name: %s%n", productName);
                }
                System.out.printf("Transport Price: $%.2f%n", tcost);
                System.out.printf("Total Price: $%.2f%n", totalPrice);
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            }
        }
        System.out.println("\nThank you for shopping!");
    }
    public boolean addProduct() throws SQLException {
        String productID, inventoryID, name, brand, category, description, dimension;
        float price, weight;
        int quantity;

        //product ID check
        do {
            System.out.print("\nEnter product name\n>> ");
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
                System.out.println("\nProduct name exists!");
            }
        } while(true);

        System.out.print("Enter brand\n>> ");
        brand =  scanner.next();
        if(brand.equals("-1")){
            return false;
        }

        System.out.print("Enter description\n>> ");

        scanner.nextLine();
        description =  scanner.nextLine();
        if(description.equals("-1")){
            return false;
        }

        System.out.print("Enter category\n>> ");
        category =  scanner.next();
        if(category.equals("-1")){
            return false;
        }

        System.out.print("Enter price\n>> ");
        price =  scanner.nextFloat();
        if(price == -1){
            return false;
        }

        System.out.print("Enter weight\n>> ");
        weight =  scanner.nextFloat();
        if(weight == -1){
            return false;
        }

        System.out.print("Enter dimension\n>> ");
        dimension =  scanner.next();
        if(dimension.equals("-1")){
            return false;
        }

        System.out.print("Enter quantity\n>> ");
        quantity = scanner.nextInt();
        if(quantity == -1){
            return false;
        }

        getStmt(conn).execute(String.format("INSERT INTO PRODUCT (PRODUCTID, NAME, CATEGORY, DESCRIPTION, PRICE, WEIGHT, DIMENSION, BRAND, STOCK_QTY, VIEWS) VALUES ('%s', '%s', '%s','%s', %f, %f, '%s', '%s',%d, 0)",productID, name, category, description, price, weight,dimension,brand,quantity ));
        getStmt(conn).execute("COMMIT");
        return true;
    }
    public boolean getProduct() throws SQLException {
        String inputStr; int input;
        do {
            System.out.print("\nEnter product name\n>> ");
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
                    System.out.println("\nInvalid product name.");
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
            System.out.println("\nTo confirm deletion. Re-enter the product name.");
            System.out.print(">> ");
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
            System.out.print("\nSelect criterion number to change\n>> ");
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
            System.out.print("\nTo confirm change. Re-enter the product name:\n>> ");
            String productName = scanner.next();
            ResultSet rset = getStmt(conn).executeQuery(String.format("SELECT COUNT(*) FROM PRODUCT WHERE PRODUCTID = '%s'",productName));
            getStmt(conn).execute("COMMIT");
            if(rset.next()){
                if(rset.getInt(1)==0){
                    return false;
                }
            }
            System.out.print("\nNew value:\n>> ");
            if(input == 7 || input == 8 || input == 9 || input == 11 || input == 12){
                getStmt(conn).execute(String.format("UPDATE PRODUCT SET %s = %d WHERE productID = '%s'",criterion,scanner.nextInt(),productName));
                getStmt(conn).execute("COMMIT");
            }
            else if (input == 2){ // promotion adding/editing
                getStmt(conn).execute(String.format("UPDATE PRODUCT SET %s = '%s' WHERE productID = '%s'",criterion,scanner.next(),productName));
                ResultSet rset2 = getStmt(conn).executeQuery(String.format("SELECT * FROM PRODUCT WHERE PRODUCTID = '%s'",productName));
                float currentPrice = 0;
                String promotionID = "";
                if(rset2.next()) {
                    currentPrice = rset2.getFloat("PRICE");
                    promotionID = rset2.getString("PROMOTIONID");
                }
                ResultSet rset3 = getStmt(conn).executeQuery(String.format("SELECT * FROM PROMOTION WHERE PROMOTIONID = '%s'",promotionID));
                float discountRate = 0;
                float discountPrice = 0;
                if(rset3.next()) {
                    discountRate = rset3.getFloat("DISCOUNTRATE");
                    discountPrice = currentPrice * (1-discountRate);
                }
                getStmt(conn).execute(String.format("UPDATE PRODUCT SET DISCOUNTPRICE = '%s' WHERE productID = '%s'",discountPrice,productName));
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
    public boolean addPromotion() throws SQLException{
        String promotionID, startDate, endDate;
        float discountRate;
        do {
            System.out.print("\nEnter promotion ID\n>> ");
            promotionID = scanner.next();
            if(promotionID.equals("-1")){
                return false;
            }
            ResultSet rset = getStmt(conn).executeQuery(String.format("SELECT COUNT(*) FROM PROMOTION WHERE PROMOTIONID = '%s'", promotionID));
            getStmt(conn).execute("COMMIT");
            if (rset.next()) {
                if (rset.getInt(1) == 0) {
                    break;
                }
                System.out.println("\nPromotion name has already existed!");
            }
        } while(true);
        System.out.print("Enter discount rate\n>> ");
        discountRate =  scanner.nextFloat();
        if(discountRate == -1){
            return false;
        }
        scanner = new Scanner(System.in);
        System.out.print("Enter start date\n>> ");
        startDate =  scanner.next();
        if(startDate.equals("-1")){
            return false;
        }
        System.out.print("Enter end date\n>> ");
        endDate =  scanner.next();
        if(endDate.equals("-1")){
            return false;
        }
        getStmt(conn).execute(String.format("INSERT INTO PROMOTION (PROMOTIONID, DISCOUNTRATE, STARTDATE, ENDDATE) VALUES ('%s', %f, to_date('%s','YYYY-MM-DD'), to_date('%s','YYYY-MM-DD'))",promotionID, discountRate, startDate, endDate));
        getStmt(conn).execute("COMMIT");
        return true;
    }
    public Boolean getPromotion() throws SQLException {
        String promotionID;
        do {
            System.out.print("Enter promotion ID\n>> ");
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
            System.out.println("\nTo confirm deletion. Re-enter the promotion ID.");
            System.out.print(">> ");
            String promotionID = scanner.next();
            ResultSet rset = getStmt(conn).executeQuery(String.format("SELECT COUNT(*) FROM PROMOTION WHERE PROMOTIONID = '%s'",promotionID));
            getStmt(conn).execute("COMMIT");
            if(rset.next()){
                if(rset.getInt(1)==0){
                    return false;
                }
            }
            getStmt(conn).execute(String.format("UPDATE PRODUCT SET PROMOTIONID = '',DISCOUNTPRICE = 0  WHERE PROMOTIONID = '%s'",promotionID));
            getStmt(conn).execute(String.format("DELETE FROM PROMOTION WHERE PROMOTIONID = '%s'",promotionID));
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
            System.out.print("\nSelect criterion number to change\n>> ");
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
            System.out.println("\nTo confirm change. Re-enter the promotion ID.");
            System.out.print(">> ");
            String promotionID = scanner.next();
            ResultSet rset = getStmt(conn).executeQuery(String.format("SELECT COUNT(*) FROM PROMOTION WHERE PROMOTIONID = '%s'",promotionID));
            getStmt(conn).execute("COMMIT");
            if(rset.next()){
                if(rset.getInt(1)==0){
                    return false;
                }
            }
            System.out.print("New value\n>> ");
            if(input == 1){
                float newRate = scanner.nextFloat();
                getStmt(conn).execute(String.format("UPDATE PROMOTION SET %s = %f WHERE PROMOTIONID = '%s'",criterion,newRate,promotionID));
                getStmt(conn).execute(String.format("UPDATE PRODUCT SET DISCOUNTPRICE = PRICE*(1- %f) WHERE PROMOTIONID = '%s'",newRate, promotionID));
                getStmt(conn).execute("COMMIT");
            }
            else{
                getStmt(conn).execute(String.format("UPDATE PROMOTION SET %s = to_date('%s','YYYY-MM-DD') WHERE productID = '%s'",criterion,scanner.next(),promotionID));
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
        System.out.println("\nWe treasure all of your responses.");
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
            System.out.println("\nHow much would you rate this product (0.0 - 5.0)");
            String input = scanner.next();
            if (!input.equals("0")){
                rating = Float.parseFloat(input);
            }
            else if(input.equals("CANCEL")){System.out.println ("\nThank You for your time"); return false;}
            else{System.out.println ("\nPlease provide a number from 0.0 - 5.0");System.out.print(">> ");}
        }
        getStmt(conn).execute(String.format("INSERT INTO REVIEW VALUES ('%s', '%s', '%s', %f)", userID,productID,comment,rating));
        getStmt(conn).execute("COMMIT");
        System.out.println ("\nThank You for your time. Your review has been successfully updated");
        return true;
    }
    public boolean changeTransportationFee() throws SQLException {
        System.out.println("\nSelect transportation fee to change");
        System.out.println("1. Basic");
        System.out.println("2. Express");
        System.out.println("0. Cancel");
        System.out.print(">> ");
        int input = scanner.nextInt();
        while(input!=1 && input!=2 && input!=0){
            System.out.println("\nInvalid input! Enter 0, 1 or 2");
            System.out.print(">> ");
            input = scanner.nextInt();
        }
        if(input == 0){
            return false;
        }
        System.out.print("\nEnter new fee\n>> ");
        float fee = scanner.nextFloat();
        while(fee<0){
            System.out.print("\nNew fee cannot be less than $0. Re-enter new fee\n>> ");
            fee = scanner.nextFloat();
        }
        if(input == 1){
            getStmt(conn).execute("UPDATE TRANSPORT SET COST = "+fee+" WHERE TRANSPORTID = 'E1'");
        }
        else if(input == 2){
            getStmt(conn).execute("UPDATE TRANSPORT SET COST = "+fee+" WHERE TRANSPORTID = 'B1'");

        }
        System.out.println("\nFee update successfully.");
        return true;
    }
    public boolean editProfile() throws SQLException {
        String input = "";
        System.out.println ("\nPlease select from the following options:");
        System.out.println("1. Change Email Address\n2. Change Password\n3. Add Address\n0. Back");
        System.out.print(">> ");
        input = scanner.next();
        while (!(input.equals("1")||input.equals("2")||input.equals("3")||input.equals("0"))){
            System.out.print("Invalid Input. Please follow the following instructions -");
            System.out.print("1 - Change Email Address | 2 - Change Password | 3 - Add Address | 0 - Back");
            System.out.print(">> ");
            input = scanner.next();
        }
        if (input.equals("0")) {return false;}
        else if (input.equals("1")) {
            // changing the user email address
            System.out.print("\nYour current email address: ");
            ResultSet rset = getStmt(conn).executeQuery(String.format("SELECT email FROM userdata WHERE USERID ='%s'", userID));
            while (rset.next()) {
                System.out.println(rset.getString(1));
            }
            System.out.println("\nContinue changing email address?");
            System.out.println("y - yes | n - no");
            System.out.print(">>");
            input = scanner.next();
            while (!(input.equals("y") || input.equals("n"))) {
                System.out.println("\nInvalid Input. Please enter y | n");
                System.out.print(">> ");
                input = scanner.next();
            }
            if (input.equals("n")) {
                return false;
            } else {
                System.out.print("\nPlease enter your new address: ");
                input = scanner.next(); // can consider checking if email address is still the same
                getStmt(conn).execute(String.format("UPDATE USERDATA SET EMAIL = '%s' WHERE USERID = '%s'", input, userID));
                getStmt(conn).execute("COMMIT");
                System.out.println("\nEmail Address has been updated successfully");
                return false;
            }
        } else if (input.equals("2")) {
            // Changing user password
            String pass1 = "";
            String pass2 = "";
            String chc = "";
            String reqPass = "";
            System.out.println("\nWARNING: You are attempting to change confidential information");
            System.out.println("Changed passwords can never be recovered.");
            System.out.println("\nDo you still wish to proceed?");
            System.out.println("y - yes | n - no");
            System.out.print(">> ");
            scanner.skip("\n");
            chc = scanner.next();
            while (!(chc.equals("y") || chc.equals("n"))) {
                System.out.print("\nInvalid Input. Please follow the following instructions -");
                System.out.println("\nContinue changing password?");
                System.out.println("y - yes | n - no");
                System.out.print(">> ");
                chc = scanner.next();
            }
            if (chc.equals("n")) {
                return false;
            } else {
                ResultSet rset = getStmt(conn).executeQuery(String.format("SELECT password FROM userdata WHERE USERID ='%s'", userID));
                System.out.println("\nSecurity Instructions - Please enter your current password");
                System.out.print(">> ");
                chc = scanner.next();
                int attempt = 5;
                if (rset.next()) {
                    reqPass = rset.getString(1);
                }
                while (!(chc.equals(reqPass)) && attempt > 1) {
                    System.out.println("\nThe password you have entered is incorrect.");
                    System.out.print("You have " + --attempt + " chances remaining");
                    System.out.println("\n Re-Enter Password");
                    System.out.print(">> ");
                    chc = scanner.next();
                }
                if (attempt == 0) {
                    System.out.println("\nRequest for Password Change has been denied");
                    return false;
                } else {
                    System.out.println("\nPlease enter your new password");
                    System.out.print(">> ");
                    pass1 = scanner.next();
                    System.out.println("\nConfirm your new password");
                    System.out.print(">> ");
                    pass2 = scanner.next();
                    attempt = 3;
                    while (!(pass1.equals(pass2)) && attempt > 1) {
                        System.out.println("\nThe password you have entered is incorrect.");
                        System.out.print("You have " + --attempt + " chances remaining");
                        System.out.println("\n Re-Enter Password");
                        System.out.print(">> ");
                        pass2 = scanner.next();
                    }
                    if (attempt == 0) {
                        System.out.println("\nRequest for Password Change has been denied");
                        return false;
                        //return false;
                    }
                    getStmt(conn).execute(String.format("UPDATE USERDATA SET PASSWORD = '%s' WHERE USERID = '%s'", pass2, userID));
                    getStmt(conn).execute("COMMIT");
                    System.out.println("\nPassword has been updated successfully");
                    return false;
                }
            }
        }else if (input.equals("3")){
            // this part is for user address adding
            String userInput= ""; String userChc = "";
            ResultSet rset = getStmt(conn).executeQuery(String.format("SELECT address FROM USERADDRESSES WHERE USERID ='%s'", userID));
            int count = 1;
            while (rset.next()) System.out.println(count++ + " " + rset.getString(1));
            System.out.println("\nWould you still like to add a new one?");
            System.out.println("\n y - yes | n - no");
            System.out.print(">> ");
            userChc = scanner.next();
            scanner.nextLine();
            while (!(userChc.equals("y")||userChc.equals("n"))){
                System.out.println("\nWrong Input. Please try again.");
                System.out.println("\n y - yes | n - no");
                System.out.print(">> ");
                userChc = scanner.next();
                scanner.nextLine();
            }
            if (userChc.equals("n")) return false;
            System.out.println("\n Please insert new address");
            System.out.print(">> ");
            userInput = scanner.nextLine();

            getStmt(conn).execute(String.format("INSERT INTO USERADDRESSES VALUES ('%s', '%s')", userID, userInput));
            getStmt(conn).execute("COMMIT");
            System.out.println ("\nAddress has been updated successfully");
        }
        return false;
    }

    // BELOW FUNCTIONS ARE USED FOR TEST CASES ->
    public static boolean userExist(String enteredUserID) throws SQLException {
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        OracleConnection connect = (OracleConnection) DriverManager.getConnection("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms", "\"22099885d\"", "jnhhpagt");
        setStmt(connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
        ResultSet rset = getStmt(connect).executeQuery(String.format("SELECT * FROM USERDATA WHERE USERID = '%s'", enteredUserID));
        while (rset.next()){
            rset.close();
            connect.close();
            return true;
        }
        getStmt(connect).execute("COMMIT");


        return true;
    }
    public static String userPwd(String enteredUserID) throws SQLException {
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        OracleConnection connect = (OracleConnection) DriverManager.getConnection("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms", "\"22099885d\"", "jnhhpagt");
        setStmt(connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
        ResultSet rset = getStmt(connect).executeQuery("SELECT password FROM USERDATA WHERE userID = '" + enteredUserID + "'");
        getStmt(connect).execute("COMMIT");
        while(rset.next()){
            return rset.getString(1);
        }
        rset.close();
        connect.close();
        return "";
    }
    public static boolean adminExist(String enteredAdminID) throws SQLException {
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        OracleConnection connect = (OracleConnection) DriverManager.getConnection("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms", "\"22099885d\"", "jnhhpagt");
        setStmt(connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
        ResultSet rset = getStmt(connect).executeQuery(String.format("SELECT COUNT(*) FROM ADMINISTRATOR WHERE adminID = '%s'", enteredAdminID));
        getStmt(connect).execute("COMMIT");
        while(rset.next() && rset.getInt(1) >= 1){
            return true;
        }
        rset.close();
        connect.close();
        return false;
    }
    public static String selectAdminPwd(String enteredAdminID) throws SQLException {
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        OracleConnection connect = (OracleConnection) DriverManager.getConnection("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms", "\"22099885d\"", "jnhhpagt");
        setStmt(connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
        ResultSet rset = getStmt(connect).executeQuery("SELECT ADMINPWD FROM ADMINISTRATOR WHERE adminID = '" + enteredAdminID + "'");
        getStmt(connect).execute("COMMIT");
        if(rset.next()){
            return rset.getString(1);
        }else {
            return "";
        }
    }
    public static boolean createUser(String enteredUserID, String firstName, String lastName, String enteredPassword,
                                     String dateOfBirth, String email, String phoneNumber, String address) throws SQLException {
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        OracleConnection connect = (OracleConnection) DriverManager.getConnection("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms", "\"22099885d\"", "jnhhpagt");
        setStmt(connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
        getStmt(connect).execute(String.format("INSERT INTO USERDATA VALUES('%s','%s','%s','%s',to_date('%s','YYYY-MM-DD'),'%s','%s')", enteredUserID, firstName, lastName, enteredPassword, dateOfBirth, email, phoneNumber));
        getStmt(connect).execute(String.format("INSERT INTO USERADDRESSES VALUES('%s','%s')", enteredUserID, address));
        getStmt(connect).execute("COMMIT");
        connect.close();
        return true;
    }
    public static boolean createAdmin(String enteredUserID, String firstName, String lastName, String enteredPassword,
                                      String dateOfBirth, String email, String phoneNumber) throws SQLException {
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        OracleConnection connect = (OracleConnection) DriverManager.getConnection("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms", "\"22099885d\"", "jnhhpagt");
        setStmt(connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
        getStmt(connect).execute(String.format("INSERT INTO ADMINISTRATOR VALUES('%s','%s','%s','%s',to_date('%s','YYYY-MM-DD'),'%s','%s')", enteredUserID, firstName, lastName, enteredPassword, dateOfBirth, email, phoneNumber));
        getStmt(connect).execute("COMMIT");
        connect.close();
        return true;
    }
    public static List<Map<String, Object>> selectReport(String orderByCol, boolean isAsc) throws SQLException {
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        OracleConnection connect = (OracleConnection) DriverManager.getConnection("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms", "\"22099885d\"", "jnhhpagt");
        setStmt(connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
        ResultSet rset = getStmt(connect).executeQuery("SELECT * FROM PRODUCT ORDER BY "+orderByCol+" " + (isAsc?"ASC":"DESC"));
        getStmt(connect).execute("COMMIT");
        List<Map<String, Object>> reports = new ArrayList<>();
        Map<String, Object> report = null;
        while(rset.next()) {
            report = new HashMap<>();
            report.put("name", rset.getString("name"));
            report.put("STOCK_QTY", rset.getInt("STOCK_QTY"));
            report.put("UNITS_SOLD", rset.getInt("UNITS_SOLD"));
            report.put("PRICE",rset.getInt("PRICE"));
            report.put("VIEWS",rset.getInt("VIEWS"));
            report.put("productID",  rset.getString("productID"));
            reports.add(report);
        }
        getStmt(connect).close();
        return reports;
    }
    public static List<Map<String, Object>> selectReportUnion() throws SQLException {
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        OracleConnection connect = (OracleConnection) DriverManager.getConnection("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms", "\"22099885d\"", "jnhhpagt");
        setStmt(connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
        ResultSet rset = getStmt(connect).executeQuery("SELECT * FROM PROMOTION t1 JOIN PRODUCT t2 ON t1.PROMOTIONID = t2.PROMOTIONID");
        getStmt(connect).execute("COMMIT");
        List<Map<String, Object>> reports = new ArrayList<>();
        Map<String, Object> report = null;
        while(rset.next()) {
            report = new HashMap<>();
            report.put("PROMOTIONID", rset.getString("PROMOTIONID"));
            report.put("NAME", rset.getString("NAME"));
            report.put("DISCOUNTRATE", rset.getFloat("DISCOUNTRATE"));
            report.put("STARTDATE", rset.getString("STARTDATE"));
            report.put("ENDDATE", rset.getString("ENDDATE"));
            report.put("STOCK_QTY", rset.getInt("STOCK_QTY"));
            report.put("UNITS_SOLD", rset.getInt("UNITS_SOLD"));
            reports.add(report);
        }
        getStmt(connect).close();
        return reports;
    }
    public static List<Map<String, Object>> selectProductBylike(String keyword) throws SQLException {
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        OracleConnection connect = (OracleConnection) DriverManager.getConnection("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms", "\"22099885d\"", "jnhhpagt");
        setStmt(connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
        String query = String.format("SELECT * FROM product WHERE name LIKE '%%%s%%' OR description LIKE '%%%s%%'", keyword, keyword);
        ResultSet rset = getStmt(connect).executeQuery(query);
        getStmt(connect).execute("COMMIT");
        List<Map<String, Object>> products = new ArrayList<>();
        Map<String, Object> product = null;
        while(rset.next()) {
            product = new HashMap<>();
            product.put("name", rset.getString("name"));
            product.put("price", rset.getDouble("price"));
            product.put("discountprice", rset.getDouble("discountprice"));
            product.put("productID", rset.getString("productID"));
            products.add(product);
        }
        connect.close();
        return products;
    }
    public static List<Map<String, Object>> selectAllProduct() throws SQLException {
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        OracleConnection connect = (OracleConnection) DriverManager.getConnection("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms", "\"22099885d\"", "jnhhpagt");
        setStmt(connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
        ResultSet rset = getStmt(connect).executeQuery("SELECT * FROM product");
        getStmt(connect).execute("COMMIT");
        List<Map<String, Object>> products = new ArrayList<>();
        Map<String, Object> product = null;
        while(rset.next()) {
            product = new HashMap<>();
            product.put("name", rset.getString("name"));
            product.put("price", rset.getDouble("price"));
            product.put("discountprice", rset.getDouble("discountprice"));
            product.put("productID", rset.getString("productID"));
            products.add(product);
        }
        connect.close();
        return products;
    }
    public static Map<String, Object> selectProductById(String productID) throws SQLException {
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        OracleConnection connect = (OracleConnection) DriverManager.getConnection("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms", "\"22099885d\"", "jnhhpagt");
        setStmt(connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
        ResultSet rset = getStmt(connect).executeQuery("SELECT * FROM product WHERE productID = '" + productID + "'");
        getStmt(connect).execute("COMMIT");
        Map<String, Object> product = null;
        if(rset.next()) {
            product = new HashMap<>();
            product.put("name", rset.getString("name"));
            product.put("price", rset.getDouble("price"));
            product.put("discountprice", rset.getDouble("discountprice"));
            product.put("productID", rset.getString("productID"));
            product.put("stock_qty", rset.getInt("stock_qty"));
            product.put("description", rset.getString("description"));
            product.put("dimension", rset.getString("dimension"));
            product.put("brand", rset.getString("brand"));
            product.put("category", rset.getString("category"));
        }
        connect.close();
        return product;
    }
    public static int selectStockQtyById(String productID) throws SQLException {
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        OracleConnection connect = (OracleConnection) DriverManager.getConnection("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms", "\"22099885d\"", "jnhhpagt");
        setStmt(connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
        ResultSet rset = getStmt(connect).executeQuery("SELECT STOCK_QTY FROM product WHERE productID = '" + productID + "'");
        getStmt(connect).execute("COMMIT");
        if(rset.next()) {
            return rset.getInt(1);
        }else{
            return -1;
        }
    }
    public static String addOrUpdateCart(int input, String userID, String productID) throws SQLException {
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        OracleConnection connect = (OracleConnection) DriverManager.getConnection("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms", "\"22099885d\"", "jnhhpagt");
        setStmt(connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
        ResultSet rset = getStmt(connect).executeQuery("SELECT productID, quantity FROM cart WHERE userID = '" + userID + "' AND productID = '" + productID + "'");
        String operation = null;
        if (rset.next()) {
            getStmt(connect).execute("UPDATE cart SET quantity = " + (input + rset.getInt("quantity")) + " WHERE userID = '" + userID + "' AND productID = '" + productID + "'");


            operation =  "update";
        } else {
            getStmt(connect).execute("INSERT INTO cart (userID, productID, quantity) VALUES ('" + userID + "', '" + productID + "', " + input + ")");
            operation = "add";
        }
        getStmt(connect).execute("COMMIT");
        connect.close();
        return operation;
    }
    public static String deleteFromCart(String userID,String productID) throws SQLException {
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        OracleConnection connect = (OracleConnection) DriverManager.getConnection("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms", "\"22099885d\"", "jnhhpagt");
        setStmt(connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
        ResultSet rset = getStmt(connect).executeQuery("SELECT name FROM product WHERE productID = '" + productID + "'");
        getStmt(connect).execute("DELETE FROM cart WHERE productID = '" + productID + "' AND userID = '" + userID + "'");
        String checkQuery = "SELECT PRODUCTID FROM orderdetails WHERE PRODUCTID = '" + productID + "'";
        ResultSet checkResult = getStmt(connect).executeQuery(checkQuery);
        getStmt(connect).execute ("COMMIT");
        if (rset.next()) {
            String productName = rset.getString("name");
            if (checkResult.next()) {
                getStmt(connect).execute("DELETE FROM orderdetails WHERE PRODUCTID = '" + productID + "' AND USERID = '" + userID + "'");
                getStmt(connect).execute ("COMMIT");
            }
            connect.close();
            return productName;
        }
        getStmt(connect).execute("COMMIT");
        return null;
    }
    public static List<Map <String, Object>> selectCartByUserId(String userID) throws SQLException {
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        OracleConnection connect = (OracleConnection) DriverManager.getConnection("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms", "\"22099885d\"", "jnhhpagt");
        setStmt(connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
        String query = "SELECT p.productID, p.name, p.price, p.discountprice, c.quantity " +
                "FROM cart c " +
                "JOIN product p ON c.productID = p.productID " +
                "WHERE c.userID = '" + userID + "'";
        ResultSet rset = getStmt(connect).executeQuery(query);
        getStmt(connect).execute("COMMIT");
        List<Map<String, Object>> carts = new ArrayList<>();
        Map<String, Object> cart = null;
        while(rset.next()) {
            cart = new HashMap<>();
            cart.put("productID", rset.getString("productID"));
            cart.put("name", rset.getString("name"));
            cart.put("quantity", rset.getInt("quantity"));
            cart.put("price", rset.getDouble("price"));
            cart.put("discountprice", rset.getDouble("discountprice"));
            carts.add(cart);
        }
        connect.close();
        return carts;
    }


}
