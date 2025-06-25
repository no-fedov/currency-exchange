package com.nefedov.currency_exchange.web.servlet;

import com.nefedov.currency_exchange.domain.service.ExchangeRateService;
import com.nefedov.currency_exchange.web.listener.AppContextListener;
import com.nefedov.currency_exchange.web.util.FormURLEncoderParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.nefedov.currency_exchange.web.servlet.JsonResponseWriter.writeJsonToResponse;

public class ExchangeRateServlet extends HttpServlet {

    private ExchangeRateService service;

    @Override
    public void init() throws ServletException {
        this.service = (ExchangeRateService) getServletContext()
                .getAttribute(AppContextListener.EXCHANGE_RATE_SERVICE_KEY);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equals("PATCH")) {
            doPatch(req, resp);
            return;
        }
        super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CurrencyPair currencyPair = extractCurrencyPair(req);
        writeJsonToResponse(resp,
                service.getByCurrenciesCode(currencyPair.baseCurrencyCode, currencyPair.targetCurrencyCode));
    }

    public void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CurrencyPair currencyPair = extractCurrencyPair(req);
        Map<String, List<String>> parameters = FormURLEncoderParser.parse(req);
        String rate = parameters.get("rate").get(0);
        writeJsonToResponse(resp,
                service.update(currencyPair.baseCurrencyCode, currencyPair.targetCurrencyCode, new BigDecimal(rate)));
    }

    private CurrencyPair extractCurrencyPair(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        String baseCurrencyCode = pathInfo.substring(1, 4);
        String targetCurrencyCode = pathInfo.substring(4);
        return new CurrencyPair(baseCurrencyCode, targetCurrencyCode);
    }

    private record CurrencyPair(String baseCurrencyCode, String targetCurrencyCode) {
    }
}
