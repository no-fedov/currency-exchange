package com.nefedov.currency_exchange.web.servlet;

import com.nefedov.currency_exchange.domain.dto.ExchangeDto;
import com.nefedov.currency_exchange.domain.service.ExchangeService;
import com.nefedov.currency_exchange.web.listener.AppContextListener;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

import static com.nefedov.currency_exchange.web.servlet.JsonResponseWriter.writeJsonToResponse;
import static com.nefedov.currency_exchange.web.util.NumberValidator.validNumber;
import static com.nefedov.currency_exchange.web.util.URLValidator.validateLength;

public class ExchangeServlet extends HttpServlet {

    private static final String CURRENCY_CODE_PATTERN = "###";
    private static final String FROM = "from";
    private static final String TO = "to";
    private static final String AMOUNT = "amount";

    private ExchangeService exchangeService;

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        this.exchangeService = (ExchangeService) servletContext.getAttribute(AppContextListener.EXCHANGE_SERVICE_KEY);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String from = req.getParameter(FROM);
        String to = req.getParameter(TO);
        String amount = req.getParameter(AMOUNT);
        validateLength(FROM, from, CURRENCY_CODE_PATTERN.length());
        validateLength(TO, to, CURRENCY_CODE_PATTERN.length());
        validNumber(AMOUNT, amount);
        ExchangeDto exchangeDto = exchangeService.convertCurrency(from, to, new BigDecimal(amount));
        writeJsonToResponse(resp, exchangeDto);
    }
}
