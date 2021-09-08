package com.lukk.exchangeapp.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
public class Rate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    private String currencyName;

    @Column(precision = 12, scale = 5)
    private BigDecimal value;

    private Date date;

    public Rate(String currencyName, BigDecimal value, Date date) {
        this.currencyName = currencyName;
        this.value = value;
        this.date = date;
    }

}
