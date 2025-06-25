package com.nefedov.currency_exchange.domain.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface Transactional<T> {

    T execute(Connection connection) throws SQLException;
}
