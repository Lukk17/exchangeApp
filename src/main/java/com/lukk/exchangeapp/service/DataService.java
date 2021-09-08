package com.lukk.exchangeapp.service;

import com.google.gson.Gson;
import com.lukk.exchangeapp.config.PropertyConfig;
import com.lukk.exchangeapp.dto.RateDTO;
import com.lukk.exchangeapp.dto.RatesResponse;
import com.lukk.exchangeapp.entity.Rate;
import com.lukk.exchangeapp.repository.DataRepository;
import lombok.Data;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Data
public class DataService {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private final RestTemplate rest;
    private final DataRepository repository;
    private final PropertyConfig propertyConfig;

    /**
     * Every hour Rates older than year will be deleted from DB.
     */
    @Scheduled(fixedDelay = 60 * 60 * 1000)
    public void clearOldRates() {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.YEAR, -1);
        Date yearAgo = cal.getTime();

        List<Rate> toDelete = repository.findAllByDateBefore(yearAgo);
        repository.deleteAll(toDelete);
    }

    /**
     * Get data from exchange service and if not already existing for given day then save it to DB.
     */
    public void downloadData() {
        RatesResponse ratesResponse = getDataFromExchange();

        if (!isAlreadyRegistered(ratesResponse)) {
            saveRates(ratesResponse);
        }
    }

    /**
     * Get Rates for given date.
     * If there is single date then only for this date Rates will be retrieved.
     * If there is range of Dates then all Rates for that range will be retrieved.
     *
     * @param dateData for which Rates will be retrieved.
     * @return List of Rates.
     * @throws ParseException when problem with date parsing.
     */
    public List<RateDTO> getRates(String dateData) throws ParseException {

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        List<Rate> rates;

        if (isRangeOfDates(dateData)) {
            String[] dateRange = dateData.split(":");

            Date startDate = formatter.parse(dateRange[0]);
            Date endDate = formatter.parse(dateRange[1]);

            rates = repository.findAllByDateAfterAndDateBefore(startDate, endDate);

        } else {
            Date date = formatter.parse(dateData);
            rates = repository.findAllByDate(date);
        }

        return convertToDTO(rates);
    }

    /**
     * Check if given String is single date or range of dates.
     *
     * @param dateData to be checked.
     * @return {@code true} if it is range of dates, otherwise {@code false}.
     */
    private boolean isRangeOfDates(String dateData) {
        return dateData.contains(":");
    }

    /**
     * Send REST request to external exchange service for current rates.
     *
     * @return Response with rate data.
     */
    private RatesResponse getDataFromExchange() {
        Gson gson = new Gson();
        HttpEntity<String> entity = buildEntity();
        String url = buildUrl();

        ResponseEntity<String> response = rest.exchange(url, HttpMethod.GET, entity, String.class);

        return gson.fromJson(response.getBody(), RatesResponse.class);
    }

    /**
     * Build Http Entity with only headers.
     *
     * @return Http Entity
     */
    private HttpEntity<String> buildEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(headers);
    }

    /**
     * Build URL with parameters from properties.
     *
     * @return URL as String.
     */
    private String buildUrl() {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(propertyConfig.getUrl())
            .queryParam("access_key", propertyConfig.getAccessKey())
            .queryParam("symbols", propertyConfig.getSymbols())
            .queryParam("base", propertyConfig.getBase());

        return uriBuilder.toUriString();
    }

    /**
     * Checks if Rates with given date are already registered.
     *
     * @param ratesResponse with Rates date.
     * @return {@code true} if already registered, otherwise {@code false}.
     */
    private boolean isAlreadyRegistered(RatesResponse ratesResponse) {
        return repository.findFirstByDateEquals(ratesResponse.getDate()) != null;
    }

    /**
     * Save all rates from exchange response to DB.
     *
     * @param ratesResponse with Rates data to be saved.
     */
    private void saveRates(RatesResponse ratesResponse) {
        List<Rate> rates = new ArrayList<>();
        Map<String, BigDecimal> map = ratesResponse.getRates();

        Date date = ratesResponse.getDate();
        map.forEach((currencyName, value) -> rates.add(new Rate(currencyName, value, date)));

        repository.saveAll(rates);
    }

    /**
     * Converts Rate objects into DTO ones.
     *
     * @param rates to be converted.
     * @return List of RateDTO objects.
     */
    private List<RateDTO> convertToDTO(List<Rate> rates) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

        return rates.stream()
            .map(rate -> new RateDTO(rate.getCurrencyName(), rate.getValue(), formatter.format(rate.getDate())))
            .collect(Collectors.toList());
    }
}
