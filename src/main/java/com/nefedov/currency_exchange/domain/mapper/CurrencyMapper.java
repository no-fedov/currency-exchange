package com.nefedov.currency_exchange.domain.mapper;

import com.nefedov.currency_exchange.domain.dto.CurrencyDto;
import com.nefedov.currency_exchange.domain.entity.Currency;

import java.util.Collection;
import java.util.List;

public class CurrencyMapper {

    private CurrencyMapper() {
    }

    public static Currency toEntity(CurrencyDto dto) {
        return new Currency(dto.getId(), dto.getCode(), dto.getFullName(), dto.getSign());
    }

    public static CurrencyDto toDto(Currency entity) {
        return CurrencyDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .fullName(entity.getFullName())
                .sign(entity.getSign())
                .build();
    }

    public static List<CurrencyDto> toDtos(Collection<Currency> entities) {
        return entities.stream()
                .map(CurrencyMapper::toDto)
                .toList();
    }
}
