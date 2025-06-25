package com.nefedov.currency_exchange.domain.service;

import com.nefedov.currency_exchange.domain.dao.CurrencyDao;
import com.nefedov.currency_exchange.domain.dao.exception.EntityNotFoundException;
import com.nefedov.currency_exchange.domain.dto.CurrencyDto;
import com.nefedov.currency_exchange.domain.entity.Currency;
import com.nefedov.currency_exchange.domain.mapper.CurrencyMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyDao currencyDao;

    public CurrencyDto create(CurrencyDto dto) {
        Currency currency = CurrencyMapper.toEntity(dto);
        Currency savedCurrency = currencyDao.save(currency);
        return CurrencyMapper.toDto(savedCurrency);
    }

    public List<CurrencyDto> getAll() {
        List<Currency> currencies = currencyDao.getAll();
        return CurrencyMapper.toDtos(currencies);
    }

    public CurrencyDto getByCode(String code) {
        Currency currency = currencyDao.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Валюта с именем '%s' не найдена".formatted(code)));
        return CurrencyMapper.toDto(currency);
    }
}
