package com.nefedov.currency_exchange.web.servlet;

import com.nefedov.currency_exchange.domain.service.ExchangeRateService;
import com.nefedov.currency_exchange.web.listener.AppContextListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

import static com.nefedov.currency_exchange.web.servlet.JsonResponseWriter.writeJsonToResponse;

public class ExchangeRatesServlet extends HttpServlet {

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
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");
        writeJsonToResponse(resp, service.create(baseCurrencyCode, targetCurrencyCode, new BigDecimal(rate)));
    }
}
