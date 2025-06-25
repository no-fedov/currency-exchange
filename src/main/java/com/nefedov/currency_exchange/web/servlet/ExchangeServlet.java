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

public class ExchangeServlet extends HttpServlet {

    private ExchangeService exchangeService;

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        this.exchangeService = (ExchangeService) servletContext.getAttribute(AppContextListener.EXCHANGE_SERVICE_KEY);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amount = req.getParameter("amount");
        ExchangeDto exchangeDto = exchangeService.convertCurrency(from, to, new BigDecimal(amount));
        writeJsonToResponse(resp, exchangeDto);
    }
}
