package com.lukk.exchangeapp.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Data
public class RatesResponse {
    boolean success;
    int timestamp;
    String base;
    Date date;
    Map<String, BigDecimal> rates;
}
