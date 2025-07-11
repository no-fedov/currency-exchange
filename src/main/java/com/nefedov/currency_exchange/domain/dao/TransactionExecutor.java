package com.nefedov.currency_exchange.domain.dao;

import com.nefedov.currency_exchange.connection.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionExecutor {

    private TransactionExecutor() {
    }

    public static <T> T doInTransaction(Transactional<T> operation, int transactionalLevel) {
        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(transactionalLevel);
            T result = operation.execute(connection);
            connection.commit();
            return result;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw SQLiteErrors.handle("Ошибка при откате транзакции", ex);
            }
            throw SQLiteErrors.handle("Ошибка в транзакции: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    throw SQLiteErrors.handle("Ошибка при закрытии соединения", e);
                }
            }
        }
    }
}
