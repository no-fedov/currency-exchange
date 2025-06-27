package com.nefedov.currency_exchange.web.util;

import com.nefedov.currency_exchange.web.exception.ValidationException;

public class URLValidator {

    public static void validFormRequest(String parameterName, String... parameters) {
        for (String parameter : parameters) {
            if (parameter.trim().isBlank()) {
                throw new ValidationException("Параметр формы %s должен быть зполнен".formatted(parameterName));
            }
        }
    }

    public static void validateLength(String parameterName,
                                      String value,
                                      int expectedLength) {
        if (value == null || value.trim().isEmpty() || value.length() < expectedLength) {
            throw new ValidationException("Параметр %s не заполнен, или заполнен некорректно".formatted(parameterName));
        }
    }
}
