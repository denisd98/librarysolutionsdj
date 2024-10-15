package com.example.librarysolutionsdj;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;

public class MySQLConnection {

    protected static String db = "biblioteca_app_db";
    protected static String ip = "10.0.2.2";
    protected static String port = "3306";
    protected static String username = "root";
    protected static String password = "1562354789";

    public Connection conn(){
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String connectionString = "jdbc:mysql://" + ip + ":" + port + "/" + db + "?connectTimeout=5000";
            conn = DriverManager.getConnection(connectionString,username,password);

        } catch (Exception e){
            Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
        }
        return conn;
    }
}