package com.lukk.exchangeapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExchangeAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExchangeAppApplication.class, args);
    }

}
