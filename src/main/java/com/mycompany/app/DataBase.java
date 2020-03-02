package com.mycompany.app;

import com.mysql.fabric.jdbc.FabricMySQLDriver;

import java.sql.*;

public class DataBase {
    public Statement setConnection() throws SQLException {
        Connection conn;
        try {
            Driver driver = new FabricMySQLDriver();
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
