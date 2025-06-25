package com.nefedov.currency_exchange.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionPool {
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection open() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:currency_exchange.db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
