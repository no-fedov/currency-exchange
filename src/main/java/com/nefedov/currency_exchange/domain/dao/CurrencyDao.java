package com.nefedov.currency_exchange.domain.dao;

import com.nefedov.currency_exchange.domain.dao.mapper.CurrencyRowMapper;
import com.nefedov.currency_exchange.domain.entity.Currency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao {

    private static final String INSERT_QUERY_TEMPLATE = """
            insert into currencies(code, full_name, sign)
            values (?, ?, ?)
            """;

    private static final String FIND_QUERY_TEMPLATE = """
            select id, code, full_name, sign
            from currencies
            """;

    private static final String FIND_ALL_QUERY = FIND_QUERY_TEMPLATE.concat("order by id");

    private static final String FIND_BY_CODE_QUERY_TEMPLATE = FIND_QUERY_TEMPLATE.concat("where code = ?");

    public Currency save(Currency entity) {
        return TransactionExecutor.doInTransaction(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(INSERT_QUERY_TEMPLATE,
                    Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, entity.getCode());
                statement.setString(2, entity.getFullName());
                statement.setString(3, entity.getSign());
                statement.executeUpdate();
                ResultSet generatedKeys = statement.getGeneratedKeys();
                entity.setId(generatedKeys.getInt(1));
                return entity;
            }
        }, Connection.TRANSACTION_READ_COMMITTED);
    }

    public List<Currency> getAll() {
        return TransactionExecutor.doInTransaction(connection -> {
            List<Currency> result = new LinkedList<>();
            try (PreparedStatement statement = connection.prepareStatement(FIND_ALL_QUERY)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    result.add(CurrencyRowMapper.toEntity(resultSet));
                }
                return Collections.unmodifiableList(result);
            }
        }, Connection.TRANSACTION_READ_COMMITTED);
    }

    public Optional<Currency> findByCode(String code) {
        return TransactionExecutor.doInTransaction(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(FIND_BY_CODE_QUERY_TEMPLATE);) {
                statement.setString(1, code);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return Optional.of(CurrencyRowMapper.toEntity(resultSet));
                } else {
                    return Optional.empty();
                }
            }
        }, Connection.TRANSACTION_READ_COMMITTED);
    }
}
