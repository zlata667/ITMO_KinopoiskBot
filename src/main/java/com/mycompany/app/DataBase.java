package com.mycompany.app;


import java.sql.*;

public class DataBase {
    public Statement setConnection() throws SQLException {
        Connection conn;
        try {
            Driver driver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(driver);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conn = DriverManager.getConnection("jdbc:mysql://remotemysql.com:3306/wssmTKXCex", "wssmTKXCex", "WhxmR8YpfY");

        Statement statement = conn.createStatement();
        return statement;
        //statement.executeUpdate("insert into Subscribes value (null, 1, 2, 'john');");

        //conn.close();
    }
}
