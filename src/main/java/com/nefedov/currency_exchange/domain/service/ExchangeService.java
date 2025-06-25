package com.nefedov.currency_exchange.domain.service;

import com.nefedov.currency_exchange.domain.dao.ExchangeRateDao;
import com.nefedov.currency_exchange.domain.dao.exception.EntityNotFoundException;
import com.nefedov.currency_exchange.domain.dto.ExchangeDto;
import com.nefedov.currency_exchange.domain.entity.ExchangeRate;
import com.nefedov.currency_exchange.domain.mapper.CurrencyMapper;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RequiredArgsConstructor
public class ExchangeService {

    private final ExchangeRateDao exchangeRateDao;

    public ExchangeDto convertCurrency(String baseCurrency, String targetCurrency, BigDecimal amount) {
        List<TriFunction<ExchangeDto, String, String, BigDecimal>> calculateExchange = List.of(
                this::calculateExchange,
                this::calculateReverseExchange,
                this::calculateExchangeByUSD
        );
        for (TriFunction<ExchangeDto, String, String, BigDecimal> strategy : calculateExchange) {
            try {
                return strategy.apply(baseCurrency, targetCurrency, amount);
            } catch (EntityNotFoundException e) {
            }
        }
        throw new EntityNotFoundException("Обменный курс между валютами %s-%s не найден"
                .formatted(baseCurrency, targetCurrency));
    }

    private ExchangeDto calculateExchange(String baseCurrency, String targetCurrency, BigDecimal amount) {
        ExchangeRate exchangeRate = exchangeRateDao.findByCurrenciesCode(baseCurrency, targetCurrency)
                .orElseThrow(EntityNotFoundException::new);
        BigDecimal rate = exchangeRate.getRate();
        BigDecimal convertedAmount = rate.multiply(amount).setScale(2, RoundingMode.HALF_UP);
        return ExchangeDto.builder()
                .rate(rate)
                .baseCurrency(CurrencyMapper.toDto(exchangeRate.getBaseCurrency()))
                .targetCurrency(CurrencyMapper.toDto(exchangeRate.getTargetCurrency()))
                .amount(amount)
                .convertedAmount(convertedAmount)
                .build();
    }

    private ExchangeDto calculateReverseExchange(String baseCurrency, String targetCurrency, BigDecimal amount) {
        ExchangeRate exchangeRate = exchangeRateDao.findByCurrenciesCode(targetCurrency, baseCurrency)
                .orElseThrow(EntityNotFoundException::new);
        BigDecimal rate = BigDecimal.ONE.divide(exchangeRate.getRate(), 2, RoundingMode.HALF_UP);
        BigDecimal convertedAmount = rate.multiply(amount).setScale(2, RoundingMode.HALF_UP);
        return ExchangeDto.builder()
                .rate(rate)
                .baseCurrency(CurrencyMapper.toDto(exchangeRate.getBaseCurrency()))
                .targetCurrency(CurrencyMapper.toDto(exchangeRate.getTargetCurrency()))
                .amount(amount)
                .convertedAmount(convertedAmount)
                .build();
    }

    private ExchangeDto calculateExchangeByUSD(String baseCurrency, String targetCurrency, BigDecimal amount) {
        ExchangeRate exchangeRate1 = exchangeRateDao.findByCurrenciesCode("USD", baseCurrency)
                .orElseThrow(EntityNotFoundException::new);
        ExchangeRate exchangeRate2 = exchangeRateDao.findByCurrenciesCode("USD", targetCurrency)
                .orElseThrow(EntityNotFoundException::new);
        BigDecimal rate = exchangeRate2.getRate().divide(exchangeRate1.getRate(), 2, RoundingMode.HALF_UP);
        BigDecimal convertedAmount = rate.multiply(amount).setScale(2, RoundingMode.HALF_UP);
        return ExchangeDto.builder()
                .rate(rate)
                .baseCurrency(CurrencyMapper.toDto(exchangeRate1.getTargetCurrency()))
                .targetCurrency(CurrencyMapper.toDto(exchangeRate2.getTargetCurrency()))
                .amount(amount)
                .convertedAmount(convertedAmount)
                .build();
    }

    private interface TriFunction<T, U, S, D> {
        T apply(U u, S s, D d);
    }
}
