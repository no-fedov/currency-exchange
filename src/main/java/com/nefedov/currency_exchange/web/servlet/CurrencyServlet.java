package com.nefedov.currency_exchange.web.servlet;

import com.nefedov.currency_exchange.domain.dto.CurrencyDto;
import com.nefedov.currency_exchange.domain.service.CurrencyService;
import com.nefedov.currency_exchange.web.exception.ValidationException;
import com.nefedov.currency_exchange.web.listener.AppContextListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

import static com.nefedov.currency_exchange.web.servlet.JsonResponseWriter.writeJsonToResponse;

public class CurrencyServlet extends HttpServlet {

    private CurrencyService currencyService;

    @Override
    public void init() throws ServletException {
        this.currencyService = (CurrencyService) getServletContext()
                .getAttribute(AppContextListener.CURRENCY_SERVICE_KEY);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        validRequest(req);
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String sign = req.getParameter("sign");
        CurrencyDto currency = new CurrencyDto(null, code, name, sign);
        CurrencyDto savedCurrency = currencyService.create(currency);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        writeJsonToResponse(resp, savedCurrency);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            writeJsonToResponse(resp, currencyService.getAll());
        } else {
            String currencyName = pathInfo.substring(1);
            writeJsonToResponse(resp, currencyService.getByName(currencyName));
        }
    }

    private void validRequest(HttpServletRequest req) {
        Enumeration<String> parameterNames = req.getParameterNames();
        Iterator<String> iterator = parameterNames.asIterator();
        while (iterator.hasNext()) {
            String parameterName = iterator.next();
            String parameterValue = req.getParameter(parameterName);
            if (parameterValue.trim().isBlank()) {
                throw new ValidationException("Параметр %s должены быть заполнен".formatted(parameterName));
            }
        }
    }
}
