package com.nefedov.currency_exchange.domain.dao;

import com.nefedov.currency_exchange.domain.dao.exception.DataBaseException;
import com.nefedov.currency_exchange.domain.dao.exception.EntityAlreadyExistsException;
import lombok.Getter;

import java.sql.SQLException;

@Getter
public enum SQLiteErrors {

    SQLITE_CONSTRAINT(19);

    private final int errorCode;

    SQLiteErrors(int errorCode) {
        this.errorCode = errorCode;
    }

    public static RuntimeException handle(String message, SQLException e) {
        if (e.getErrorCode() == SQLITE_CONSTRAINT.getErrorCode()) {
            throw new EntityAlreadyExistsException("Нельзя добавить такую же сущность", e);
        }
        return new DataBaseException(message, e);
    }
}
