package com.lukk.exchangeapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RateDTO {

    private String currencyName;
    private BigDecimal value;
    private String date;
}
