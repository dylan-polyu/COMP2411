import java.sql.*;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) throws SQLException {
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
            String input = scanner.next();
            if(input.equals("0")) {
                oss.closeApp();
                break;
            }
            switch (input) {
                case "0":
                    loginFlag = true;
                    break;
                case "1":
                    String loginType;
                    do{
                        System.out.println("\nAre you a user or admin?");
                        System.out.print("'u' - user account | 'a' - admin account\n>> ");
                        loginType = scanner.next();
                        if(loginType.equals("u") || loginType.equals("a")){
                            break;
                        }
                        else{
                            System.out.print("Incorrect input. Please try again.\n");
                        }
                    } while(true);
                    if(loginType.equals("a")){
                        loginFlag = oss.loginAdmin();
                        if(loginFlag)userType = "a";
                    }
                    else{
                        loginFlag = oss.loginUser();
                        if(loginFlag) userType = "u";
                    }
                    break;
                case "2":
                    String createType;
                    do{
                        System.out.println("\nCreate user or admin account?");
                        System.out.println("\n'u' - user account | 'a' - admin account\n>> ");
                        createType = scanner.next();
                        if(createType.equals("u") || createType.equals("a")){
                            break;
                        }
                        else{
                            System.out.println("\nIncorrect input. Please try again.\n>> ");
                        }
                    } while(true);
                    if(createType.equals("a")){
                        oss.createAdmin();
                    }
                    else{
                        oss.createUser();
                    }
                    break;
                default:
                    System.out.println("Invalid input.");
                    break;
            }
            if(userType.equals("a")){
                AdministratorApp adminapp = new AdministratorApp(oss);
                adminapp.run();
            }
            else if(userType.equals("u")){
                UserApp userapp = new UserApp(oss);
                userapp.run();
            }
        }

    }
}
