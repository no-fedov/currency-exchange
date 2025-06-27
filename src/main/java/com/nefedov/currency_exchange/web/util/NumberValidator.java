package com.nefedov.currency_exchange.web.util;

import com.nefedov.currency_exchange.web.exception.ValidationException;

public class NumberValidator {

    public static void validNumber(String parameterName, String number) {
        try {
            double asNumber = Double.parseDouble(number);
            if (asNumber < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("Параметр %s должен быть положительным числом".formatted(parameterName));
        }
    }
}
