package com.lukk.exchangeapp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "exchange")
@Getter
@Setter
public class PropertyConfig {
    private String accessKey;
    private String url;
    private String symbols;
    private String base;
}
