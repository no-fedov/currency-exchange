package com.nefedov.currency_exchange.domain.service;

import com.nefedov.currency_exchange.domain.dao.ExchangeRateDao;
import com.nefedov.currency_exchange.domain.dto.ExchangeRateDto;
import com.nefedov.currency_exchange.domain.entity.ExchangeRate;
import com.nefedov.currency_exchange.domain.mapper.ExchangeRateMapper;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class ExchangeRateService {

    private final ExchangeRateDao exchangeRateDao;

    public ExchangeRateDto create(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        ExchangeRate savedExchangeRate = exchangeRateDao.save(baseCurrencyCode, targetCurrencyCode, rate);
        return ExchangeRateMapper.toDto(savedExchangeRate);
    }

    public List<ExchangeRateDto> getAll() {
        List<ExchangeRate> exchangeRates = exchangeRateDao.getAll();
        return ExchangeRateMapper.toDtos(exchangeRates);
    }

    public ExchangeRateDto update(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        ExchangeRate updatedExchangeRate = exchangeRateDao.update(baseCurrencyCode, targetCurrencyCode, rate);
        return ExchangeRateMapper.toDto(updatedExchangeRate);
    }

    public ExchangeRateDto getByCurrenciesCode(String baseCurrencyCode, String targetCurrencyCode) {
        ExchangeRate exchangeRate = exchangeRateDao.findByCurrenciesCode(baseCurrencyCode, targetCurrencyCode);
        return ExchangeRateMapper.toDto(exchangeRate);
    }
}
