package com.lukk.exchangeapp.controler;

import com.lukk.exchangeapp.dto.RateDTO;
import com.lukk.exchangeapp.service.DataService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

@RestController
@AllArgsConstructor
public class DataController {

    private final DataService dataService;

    @GetMapping("/download")
    public ResponseEntity<Object> download() {
        dataService.downloadData();
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/presentData")
    public ResponseEntity<Object> presentData(@RequestParam(value = "date", required = false) String date) throws ParseException {
        List<RateDTO> rates = dataService.getRates(date);
        return new ResponseEntity<>(rates, HttpStatus.OK);
    }

}
