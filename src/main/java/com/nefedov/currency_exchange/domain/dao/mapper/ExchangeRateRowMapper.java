package com.nefedov.currency_exchange.domain.dao.mapper;

import com.nefedov.currency_exchange.domain.entity.Currency;
import com.nefedov.currency_exchange.domain.entity.ExchangeRate;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExchangeRateRowMapper {

    private ExchangeRateRowMapper() {
    }

    public static ExchangeRate toEntity(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        Currency baseCurrency = CurrencyRowMapper.toEntity(rs, "base_currency_");
        Currency targetCurrency = CurrencyRowMapper.toEntity(rs, "target_currency_");
        BigDecimal rate = rs.getBigDecimal("rate");
        return new ExchangeRate(id, baseCurrency, targetCurrency, rate);
    }
}
