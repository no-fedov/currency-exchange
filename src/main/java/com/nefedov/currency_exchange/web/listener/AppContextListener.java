package com.nefedov.currency_exchange.web.listener;

import com.nefedov.currency_exchange.domain.dao.CurrencyDao;
import com.nefedov.currency_exchange.domain.dao.ExchangeRateDao;
import com.nefedov.currency_exchange.domain.service.CurrencyService;
import com.nefedov.currency_exchange.domain.service.ExchangeRateService;
import com.nefedov.currency_exchange.domain.service.ExchangeService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class AppContextListener implements ServletContextListener {

    public static final String CURRENCY_SERVICE_KEY = "currencyService";
    public static final String EXCHANGE_RATE_SERVICE_KEY = "exchangeRateService";
    public static final String EXCHANGE_RATE_DAO_KEY = "exchangeDao";
    public static final String EXCHANGE_SERVICE_KEY = "exchangeService";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // TODO: создать пул соединений
        initCurrencyService(sce);
        initExchangeRateService(sce);
        initExchangeService(sce);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // TODO: закрыть соединения с БД
    }

    private void initCurrencyService(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        CurrencyDao currencyDao = new CurrencyDao();
        CurrencyService currencyService = new CurrencyService(currencyDao);
        servletContext.setAttribute(CURRENCY_SERVICE_KEY, currencyService);
    }

    private void initExchangeRateService(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
        servletContext.setAttribute(EXCHANGE_RATE_DAO_KEY, exchangeRateDao);
        ExchangeRateService exchangeRateService = new ExchangeRateService(exchangeRateDao);
        servletContext.setAttribute(EXCHANGE_RATE_SERVICE_KEY, exchangeRateService);
    }

    private void initExchangeService(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        ExchangeRateDao exchangeRateDao = (ExchangeRateDao) servletContext.getAttribute(EXCHANGE_RATE_DAO_KEY);
        ExchangeService exchangeService = new ExchangeService(exchangeRateDao);
        servletContext.setAttribute(EXCHANGE_SERVICE_KEY, exchangeService);
    }
}
