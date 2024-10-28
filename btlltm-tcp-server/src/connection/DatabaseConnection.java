package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
	
    private String jdbcURL = "jdbc:mysql://localhost:3306/laptrinhmang";
    private String jdbcUsername = "databaseuser";
    private String jdbcPassword = "123456";

    private static DatabaseConnection instance;
    private Connection connection;

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    private DatabaseConnection() {

    }
    
    public Connection getConnection() {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
                System.out.println("Successfully connected to database");
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return connection;
	}
    
//    public static Connection getConnection() {
//            try {
//                String jdbcURL = "jdbc:mysql://localhost:3306/laptrinhmang";
//                String jdbcUsername = "databaseuser";
//                String jdbcPassword = "123456";
//
//                Class.forName("com.mysql.cj.jdbc.Driver");
//                return DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//            return null;
//	}
    
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    
//    public static void main(String[] args){
//        Connection conn= getConnection();
//        if(conn != null){
//            System.out.println("Successfully connected to database!");
//        }
//        else{
//            System.out.println("You failed!");
//        }
//    }
}
    

