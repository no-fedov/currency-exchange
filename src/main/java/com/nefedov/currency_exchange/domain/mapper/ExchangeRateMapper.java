package com.nefedov.currency_exchange.domain.mapper;

import com.nefedov.currency_exchange.domain.dto.ExchangeRateDto;
import com.nefedov.currency_exchange.domain.entity.ExchangeRate;

import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;

public class ExchangeRateMapper {

    private ExchangeRateMapper() {
    }

    public static ExchangeRateDto toDto(ExchangeRate entity) {
        return ExchangeRateDto.builder()
                .id(entity.getId())
                .baseCurrency(CurrencyMapper.toDto(entity.getBaseCurrency()))
                .targetCurrency(CurrencyMapper.toDto(entity.getTargetCurrency()))
                .rate(entity.getRate().setScale(2, RoundingMode.HALF_UP))
                .build();
    }

    public static List<ExchangeRateDto> toDtos(Collection<ExchangeRate> entities) {
        return entities.stream()
                .map(ExchangeRateMapper::toDto)
                .toList();
    }

    public static ExchangeRate toEntity(ExchangeRateDto dto) {
        return ExchangeRate.builder()
                .id(dto.getId())
                .baseCurrency(CurrencyMapper.toEntity(dto.getBaseCurrency()))
                .targetCurrency(CurrencyMapper.toEntity(dto.getTargetCurrency()))
                .rate(dto.getRate().setScale(2, RoundingMode.HALF_UP))
                .build();
    }
}
