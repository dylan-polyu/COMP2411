import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) throws SQLException, IOException {
        Scanner scanner = new Scanner(System.in);
        OSS oss = new OSS();
        boolean loginFlag = false;
        String userType = "";

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
                    oss.closeApp();
                    break;
                case "1":
                    String loginType;
                    do{
                        System.out.println("Are you a user or admin? (u for user / a for admin) >> ");
                        loginType = scanner.next();
                        if(loginType.equals("u") || loginType.equals("a")){
                            break;
                        }
                        else{
                            System.out.println("Incorrect input. Please try again. >> ");
                        }
                    } while(true);
                    if(loginType.equals("a")){
                        loginFlag = oss.loginAdminAccount();
                        if(loginFlag) {
                            userType = "a";
                        }
                    }
                    else{
                        loginFlag = oss.loginUserAccount();
                        if(loginFlag) userType = "u";
                    }
                    break;

                case "2":
                    String createType;
                    do{
                        System.out.println("Create user or admin account? (u for user/ a for admin) >> ");
                        createType = scanner.next();
                        if(createType.equals("u") || createType.equals("a")){
                            break;
                        }
                        else{
                            System.out.println("Incorrect input. Please try again. >> ");
                        }
                    } while(true);
                    if(createType.equals("a")){
                        loginFlag = oss.createAdminAccount();
                    }
                    else{
                        loginFlag = oss.createUserAccount();
                    }
                    break;
                default:
                    System.out.println("Invalid input.");
                    break;
            }
            if(loginFlag){
                break;
            }
        }


        if(userType.equals("a")){
            AdministratorApp adminapp = new AdministratorApp(oss);
            adminapp.run();
        }
        else if(userType.equals("u")){
            UserApp userapp = new UserApp(oss);
            userapp.run();
        }
        oss.closeApp();

    }
}
