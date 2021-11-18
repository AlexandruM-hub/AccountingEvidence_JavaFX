package sample;

import java.sql.*;

public class DatabaseConnection {
    public Connection databaseLink;

    public Connection getConnection(){
        String mysqlConnUrl = "jdbc:mysql://localhost/gtmazureac";
        String mysqlUserName = "root";
        String mysqlPassword = "";
        try{
            databaseLink = DriverManager.getConnection(mysqlConnUrl, mysqlUserName, mysqlPassword);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return databaseLink;
    }

}
