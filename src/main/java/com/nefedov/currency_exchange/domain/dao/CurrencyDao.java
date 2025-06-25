package com.nefedov.currency_exchange.domain.dao;

import com.nefedov.currency_exchange.domain.dao.mapper.CurrencyRowMapper;
import com.nefedov.currency_exchange.domain.entity.Currency;
import com.nefedov.currency_exchange.domain.dao.exception.EntityNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
            PreparedStatement statement = connection.prepareStatement(INSERT_QUERY_TEMPLATE,
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, entity.getCode());
            statement.setString(2, entity.getFullName());
            statement.setString(3, entity.getSign());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            entity.setId(generatedKeys.getInt(1));
            return entity;
        });
    }

    public List<Currency> getAll() {
        return TransactionExecutor.doInTransaction(connection -> {
            List<Currency> result = new LinkedList<>();
            PreparedStatement statement = connection.prepareStatement(FIND_ALL_QUERY);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(CurrencyRowMapper.toEntity(resultSet));
            }
            return Collections.unmodifiableList(result);
        });
    }

    public Currency findByCode(String code) {
        return TransactionExecutor.doInTransaction(connection -> {
            PreparedStatement statement = connection.prepareStatement(FIND_BY_CODE_QUERY_TEMPLATE);
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return CurrencyRowMapper.toEntity(resultSet);
            } else {
                throw new EntityNotFoundException("Валюта с именем '%s' не найдена".formatted(code));
            }
        });
    }
}
