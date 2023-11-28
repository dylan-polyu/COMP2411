import java.io.*;
import java.io.Console;
import java.sql.*;
import java.util.Scanner;
import oracle.jdbc.driver.*;
import oracle.sql.*;


public class Application {
    public static void main(String[] args) throws SQLException, IOException {
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("Welcome to PolyShop, a friendly online shopping system. Please choose the following options: ");
            System.out.println("1. login  2. create account");
            String input = scanner.nextLine();
            switch(input){
                case "1":
                    OSS.loginAccount();
                case "2":
                    OSS.createAccount();
            }
        }


    }
}