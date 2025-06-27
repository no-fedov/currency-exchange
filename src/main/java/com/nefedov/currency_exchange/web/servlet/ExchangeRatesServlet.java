package com.nefedov.currency_exchange.web.servlet;

import com.nefedov.currency_exchange.domain.service.ExchangeRateService;
import com.nefedov.currency_exchange.web.listener.AppContextListener;
import com.nefedov.currency_exchange.web.util.URLValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

import static com.nefedov.currency_exchange.web.servlet.JsonResponseWriter.writeJsonToResponse;
import static com.nefedov.currency_exchange.web.util.NumberValidator.validNumber;
import static com.nefedov.currency_exchange.web.util.URLValidator.validateLength;

public class ExchangeRatesServlet extends HttpServlet {

    private static final String CURRENCY_CODE_PATTERN = "###";
    private static final String BASE_CURRENCY_CODE = "baseCurrencyCode";
    private static final String TARGET_CURRENCY_CODE = "targetCurrencyCode";
    private static final String RATE = "rate";

    private ExchangeRateService service;

    @Override
    public void init() throws ServletException {
        this.service = (ExchangeRateService) getServletContext()
                .getAttribute(AppContextListener.EXCHANGE_RATE_SERVICE_KEY);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        writeJsonToResponse(resp, service.getAll());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getParameterMap().forEach(URLValidator::validFormRequest);
        String baseCurrencyCode = req.getParameter(BASE_CURRENCY_CODE);
        String targetCurrencyCode = req.getParameter(TARGET_CURRENCY_CODE);
        String rate = req.getParameter(RATE);
        validateLength(BASE_CURRENCY_CODE, baseCurrencyCode, CURRENCY_CODE_PATTERN.length());
        validateLength(TARGET_CURRENCY_CODE, targetCurrencyCode, CURRENCY_CODE_PATTERN.length());
        validNumber(RATE, rate);
        writeJsonToResponse(resp, service.create(baseCurrencyCode, targetCurrencyCode, new BigDecimal(rate)));
    }
}
