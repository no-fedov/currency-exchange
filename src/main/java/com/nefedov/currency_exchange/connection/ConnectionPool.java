package com.nefedov.currency_exchange.connection;

import com.nefedov.currency_exchange.PropertiesUtil;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public final class ConnectionPool {

    private static final String DB_DRIVER_KEY = "db.driver";
    private static final String DB_URL_KEY = "db.url";
    private static final String DB_POOL_SIZE_KEY = "db.pool.size";

    private static List<Connection> connections;
    private static BlockingQueue<Connection> pool;

    static {
        loadDriver();
        initConnectionPool();
    }

    private ConnectionPool() {
    }

    public static Connection getConnection() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closePool() {
        connections.forEach(e -> {
            try {
                e.close();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private static void loadDriver() {
        try {
            Class.forName(PropertiesUtil.get(DB_DRIVER_KEY));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initConnectionPool() {
        String poolSize = PropertiesUtil.get(DB_POOL_SIZE_KEY);
        int size = poolSize == null ? 10 : Integer.parseInt(poolSize);
        connections = new ArrayList<>();
        pool = new ArrayBlockingQueue<>(size);
        for (int i = 0; i < size; i++) {
            Connection connection = open();
            connections.add(connection);
            pool.add(getProxyConnection(connection));
        }
    }

    private static Connection open() {
        try {
            return DriverManager.getConnection(PropertiesUtil.get(DB_URL_KEY));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Connection getProxyConnection(Connection connection) {
        return (Connection) Proxy.newProxyInstance(ConnectionPool.class.getClassLoader(),
                new Class[]{Connection.class},
                (proxy, method, args) ->
                        method.getName().equals("close")
                                ? pool.add((Connection) proxy)
                                : method.invoke(connection, args));
    }
}
