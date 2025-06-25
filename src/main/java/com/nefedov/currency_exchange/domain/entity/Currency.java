package com.nefedov.currency_exchange.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Currency {
    private Integer id;
    private String code;
    private String fullName;
    private String sign;
}
