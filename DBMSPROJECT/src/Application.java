import java.io.*;
import java.io.Console;
import java.sql.*;
import java.util.Scanner;
import oracle.jdbc.driver.*;
import oracle.sql.*;


public class Application
{
    public static void main(String args[]) throws SQLException, IOException
    {
        Scanner scanner = new Scanner(System.in);
        OSS oss = new OSS();


        while(true){
            System.out.println("1. login  2. create account  3. delete acc");
            String input = scanner.nextLine();
            switch(input){
                case "1":
                    boolean check = true;
                    String username = "";
                    String password;
                    while(check) {
                        System.out.print("Input username:");
                        username = scanner.next();
                        System.out.print("Input password:");
                        password = scanner.next();
                        if(OSS.checkusername(username)) {
                            check = !OSS.validatePassword(username, password);
                        }
                    }
                    System.out.println("Successfully logged in.");
                    break;


                case "2":
                    boolean check = true;
                    String username = "";
                    String password;
                    while(check){
                        System.out.print("Input username: ");
                        username = scanner.next();
                        check = OSS.checkusername(username);
                        if(check) {
                             System.out.println("Username exists!");
                            System.out.println ();
                        }
                    }
                    System.out.print("Input password: ");
                    password = scanner.next();
                    OSS.createAccount(username, password);
                    System.out.println ();

                    break;
            }
        }


    }
}