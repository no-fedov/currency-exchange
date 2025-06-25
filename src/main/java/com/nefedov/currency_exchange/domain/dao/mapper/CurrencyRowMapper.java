package com.nefedov.currency_exchange.domain.dao.mapper;

import com.nefedov.currency_exchange.domain.entity.Currency;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CurrencyRowMapper {

    private CurrencyRowMapper() {
    }

    public static Currency toEntity(ResultSet rs) throws SQLException {
        return toEntity(rs, "");
    }

    public static Currency toEntity(ResultSet rs, String prefix) throws SQLException {
        int id = rs.getInt(prefix + "id");
        String code = rs.getString(prefix + "code");
        String fullName = rs.getString(prefix + "full_name");
        String sign = rs.getString(prefix + "sign");
        return new Currency(id, code, fullName, sign);
    }
}
