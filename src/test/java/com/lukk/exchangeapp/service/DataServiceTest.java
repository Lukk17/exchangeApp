package com.lukk.exchangeapp.service;

import com.google.gson.Gson;
import com.lukk.exchangeapp.config.PropertyConfig;
import com.lukk.exchangeapp.dto.RateDTO;
import com.lukk.exchangeapp.dto.RatesResponse;
import com.lukk.exchangeapp.entity.Rate;
import com.lukk.exchangeapp.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.lukk.exchangeapp.service.DataService.DATE_FORMAT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles({"test"})
class DataServiceTest {

    @Mock
    DataRepository repository;

    @Mock
    RestTemplate rest;

    @Mock
    PropertyConfig propertyConfig;

    @InjectMocks
    DataService dataService;

    @BeforeEach
    public void setup() {
        doReturn("http://test.url").when(propertyConfig).getUrl();
        doReturn("testKey").when(propertyConfig).getAccessKey();
        doReturn("testSymbols").when(propertyConfig).getSymbols();
        doReturn("testBase").when(propertyConfig).getBase();
    }

    @Test
    void clearOldRates() {
        //GIVEN
        List<Rate> toDelete = List.of(new Rate("USD", BigDecimal.ONE, new Date()));
        doReturn(toDelete).when(repository).findAllByDateBefore(any());

        //WHEN
        dataService.clearOldRates();

        //THEN
        verify(repository).deleteAll(toDelete);
    }

    @Test
    void downloadData() {
        //GIVEN
        HttpEntity<String> entity = createEntity();
        ResponseEntity<String> response = createResponse();

        doReturn(response).when(rest).exchange(anyString(), eq(HttpMethod.GET), eq(entity), eq(String.class));

        //WHEN
        dataService.downloadData();

        //THEN
        verify(repository, times(1)).saveAll(any());
    }


    @Test
    void getRates_whenRange() throws ParseException {
        //GIVEN
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        Rate rate = new Rate("USD", BigDecimal.ONE, formatter.parse("2021-09-05"));
        List<Rate> rates = List.of(rate);
        List<RateDTO> expected = List.of(
            new RateDTO(rate.getCurrencyName(), rate.getValue(), formatter.format(rate.getDate())));

        Date startDate = formatter.parse("2021-09-01");
        Date endDate = formatter.parse("2021-09-08");

        String dates = formatter.format(startDate) + ":" + formatter.format(endDate);

        doReturn(rates).when(repository).findAllByDateAfterAndDateBefore(startDate, endDate);

        //WHEN
        List<RateDTO> actual = dataService.getRates(dates);

        //THEN
        assertEquals(expected, actual);
    }

    @Test
    void getRates_whenSingleDate() throws ParseException {
        //GIVEN
        String date = "2021-09-05";
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        Rate rate = new Rate("USD", BigDecimal.ONE, formatter.parse(date));

        List<Rate> rates = List.of(rate);
        List<RateDTO> expected = List.of(
            new RateDTO(rate.getCurrencyName(), rate.getValue(), formatter.format(rate.getDate())));

        doReturn(rates).when(repository).findAllByDate(formatter.parse(date));

        //WHEN
        List<RateDTO> actual = dataService.getRates(date);

        //THEN
        assertEquals(expected, actual);
    }

    private HttpEntity<String> createEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(headers);
    }

    private ResponseEntity<String> createResponse() {
        Gson gson = new Gson();
        RatesResponse rateResponse = new RatesResponse();
        rateResponse.setDate(new Date());
        rateResponse.setBase("EUR");
        rateResponse.setSuccess(true);
        rateResponse.setTimestamp(11111);
        rateResponse.setRates(Map.of("USD", BigDecimal.ONE));

        return new ResponseEntity<>(gson.toJson(rateResponse), HttpStatus.OK);
    }
}