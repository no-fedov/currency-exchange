package com.nefedov.currency_exchange.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyDto {
    private Integer id;
    private String code;
    @JsonProperty("name")
    private String fullName;
    private String sign;
}
