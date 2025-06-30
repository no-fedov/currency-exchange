package com.nefedov.currency_exchange.web.servlet;

import com.nefedov.currency_exchange.domain.dto.CurrencyDto;
import com.nefedov.currency_exchange.domain.service.CurrencyService;
import com.nefedov.currency_exchange.web.listener.AppContextListener;
import com.nefedov.currency_exchange.web.util.URLValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.nefedov.currency_exchange.web.servlet.JsonResponseWriter.writeJsonToResponse;
import static com.nefedov.currency_exchange.web.util.URLValidator.validateLength;

public class CurrencyServlet extends HttpServlet {

    private static final int MAX_FORM_SIZE_PARAMETER = 20;
    private static final String CURRENCY_CODE_PATTERN = "###";
    private static final String CODE = "code";
    private static final String NAME = "name";
    private static final String SIGN = "sign";

    private CurrencyService currencyService;

    @Override
    public void init() throws ServletException {
        this.currencyService = (CurrencyService) getServletContext()
                .getAttribute(AppContextListener.CURRENCY_SERVICE_KEY);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getParameterMap().forEach(URLValidator::validFormRequest);
        String code = req.getParameter(CODE);
        String name = req.getParameter(NAME);
        String sign = req.getParameter(SIGN);
        validateLength(CODE, code, CURRENCY_CODE_PATTERN.length());
        validateLength(NAME, name, MAX_FORM_SIZE_PARAMETER);
        validateLength(SIGN, sign, MAX_FORM_SIZE_PARAMETER);
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
            String code = pathInfo.substring(1);
            validateLength(CODE, code, CURRENCY_CODE_PATTERN.length());
            writeJsonToResponse(resp, currencyService.getByCode(code));
        }
    }
}
