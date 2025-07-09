package com.nefedov.currency_exchange.domain.dao;

import com.nefedov.currency_exchange.connection.ConnectionPool;
import com.nefedov.currency_exchange.domain.dao.exception.EntityNotFoundException;
import com.nefedov.currency_exchange.domain.dao.mapper.ExchangeRateRowMapper;
import com.nefedov.currency_exchange.domain.entity.ExchangeRate;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDao {

    private static final String INSERT_QUERY_TEMPLATE = """
            with base_target_currencies_id as (
            select c1.id as base_currency_id, c2.id as target_currency_id
            from currencies as c1, currencies as c2
            where c1.code = ? and c2.code = ?
            )
            insert into exchange_rates(base_currency_id, target_currency_id, rate)
            select base_currency_id, target_currency_id, ?
            from base_target_currencies_id
            """;

    private static final String UPDATE_QUERY_TEMPLATE = """
            update exchange_rates
            set rate = ?
            where base_currency_id = (select id from currencies where code = ?)
            and target_currency_id = (select id from currencies where code = ?)
            """;

    private static final String FIND_QUERY_TEMPLATE = """
            select t1.id,
            t2.id as base_currency_id,
            t2.code as base_currency_code,
            t2.full_name as base_currency_full_name,
            t2.sign as base_currency_sign,
            t3.id as target_currency_id,
            t3.code as target_currency_code,
            t3.full_name as target_currency_full_name,
            t3.sign as target_currency_sign,
            t1.rate
            from exchange_rates as t1
            join currencies as t2 on t1.base_currency_id = t2.id
            join currencies as t3 on t1.target_currency_id = t3.id
            """;

    private static final String FIND_BY_CURRENCIES_CODE_QUERY_TEMPLATE = FIND_QUERY_TEMPLATE
            .concat("where base_currency_code = ? and target_currency_code = ?");

    private static String FIND_BY_ID_QUERY_TEMPLATE = FIND_QUERY_TEMPLATE.concat("where t1.id = ?");

    private static final String FIND_ALL_QUERY = FIND_QUERY_TEMPLATE.concat("order by t1.id");

    public ExchangeRate save(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        return TransactionExecutor.doInTransaction(connection -> {
            try (PreparedStatement insertStatement = connection.prepareStatement(INSERT_QUERY_TEMPLATE,
                    Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement findStatement = connection.prepareStatement(FIND_BY_ID_QUERY_TEMPLATE)) {
                insertStatement.setString(1, baseCurrencyCode);
                insertStatement.setString(2, targetCurrencyCode);
                insertStatement.setBigDecimal(3, rate);
                insertStatement.executeUpdate();

                // TODO: как будто это все лишнее и решается ограничениями в БД
                ResultSet generatedKeys = insertStatement.getGeneratedKeys();
                Integer exchangeRateId = null;
                if (generatedKeys.next()) {
                    exchangeRateId = generatedKeys.getInt(1);
                } else {
                    connection.rollback();
                    throw new EntityNotFoundException("Одной из валюты не существует (%s, %s)"
                            .formatted(baseCurrencyCode, targetCurrencyCode));
                }
                // TODO: стоит ли в одной транзакции вставлять и искать? это же не атомарные операции
                findStatement.setInt(1, exchangeRateId);
                ResultSet resultSetExchangeRate = findStatement.executeQuery();
                resultSetExchangeRate.next();
                return ExchangeRateRowMapper.toEntity(resultSetExchangeRate);
            }
        }, Connection.TRANSACTION_READ_COMMITTED);
    }

    public List<ExchangeRate> getAll() {
        return TransactionExecutor.doInTransaction(connection -> {
            List<ExchangeRate> result = new LinkedList<>();
            try (PreparedStatement statement = connection.prepareStatement(FIND_ALL_QUERY)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    result.add(ExchangeRateRowMapper.toEntity(resultSet));
                }
                return result;
            }
        }, Connection.TRANSACTION_READ_COMMITTED);
    }

    public ExchangeRate update(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        return TransactionExecutor.doInTransaction(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY_TEMPLATE)) {
                statement.setBigDecimal(1, rate);
                statement.setString(2, baseCurrencyCode);
                statement.setString(3, targetCurrencyCode);
                int executeUpdate = statement.executeUpdate();
                if (executeUpdate == 0) {
                    // TODO: нафига я здесь сделал ролбек?
                    connection.rollback();
                    throw new EntityNotFoundException("Одной из валюты не существует (%s, %s)"
                            .formatted(baseCurrencyCode, targetCurrencyCode));
                }
                return getExchangeRateByCurrencies(baseCurrencyCode,
                        targetCurrencyCode,
                        connection).orElseThrow();
            }
        }, Connection.TRANSACTION_READ_COMMITTED);
    }

    public Optional<ExchangeRate> findByCurrenciesCode(String baseCurrencyCode, String targetCurrencyCode) {
        try (Connection connection = ConnectionPool.getConnection()) {
            return getExchangeRateByCurrencies(baseCurrencyCode, targetCurrencyCode, connection);
        } catch (SQLException e) {
            throw SQLiteErrors.handle("", e);
        }
    }

    private Optional<ExchangeRate> getExchangeRateByCurrencies(String baseCurrencyCode,
                                                               String targetCurrencyCode,
                                                               Connection connection) throws SQLException {
        try (PreparedStatement findStatement = connection.prepareStatement(FIND_BY_CURRENCIES_CODE_QUERY_TEMPLATE)) {
            findStatement.setString(1, baseCurrencyCode);
            findStatement.setString(2, targetCurrencyCode);
            ResultSet resultSet = findStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(ExchangeRateRowMapper.toEntity(resultSet));
            } else {
                return Optional.empty();
            }
        }
    }
}
