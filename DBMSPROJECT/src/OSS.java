import java.io.*;
import java.io.Console;
import java.sql.*;
import oracle.jdbc.driver.*;
import oracle.sql.*;

public class OSS {
    // Connection
    static Statement stmt;
   public OSS() throws SQLException, IOException{
       DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
       OracleConnection conn = (OracleConnection)DriverManager.getConnection(
               "jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms","\"22099641d\"","slgasjkd");
       stmt = conn.createStatement();
//       String query = "CREATE TABLE usertest2("
//               + "username VARCHAR (20) NOT NULL, "
//               + "password VARCHAR (20) NOT NULL) ";
//       stmt.execute(query);
       System.out.println("Table created successfully");
   }
    public static boolean checkusername (String username) throws SQLException, IOException {
//       ResultSet rset = stmt.executeQuery("SELECT COUNT(*) FROM usertest WHERE username ='" + username + "';");
       ResultSet rset = stmt.executeQuery("SELECT COUNT(*) FROM usertest where username = '"+username+"'");
       int x = 0;
       while (rset.next()){
           x = (rset.getInt(1));
       }
       return (x>=1);
    }

    public static void createAccount(String username, String password) throws SQLException, IOException{
       stmt.execute("INSERT INTO usertest\nVALUES('"+username+"', '"+password+"')");
        System.out.println("User account created");

    }

    public static boolean validatePassword(String username, String password)throws SQLException, IOException{
       ResultSet rset = stmt.executeQuery("SELECT password FROM usertest WHERE username = '"+username+"'");
       String pass = "";
        while (rset.next()){
            pass = (rset.getString(1));
        }
        return (pass.equals(password));
    }
}
