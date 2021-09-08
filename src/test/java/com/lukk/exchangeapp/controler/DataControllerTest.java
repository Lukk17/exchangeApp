package com.lukk.exchangeapp.controler;

import com.google.gson.Gson;
import com.lukk.exchangeapp.dto.RateDTO;
import com.lukk.exchangeapp.service.DataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.lukk.exchangeapp.service.DataService.DATE_FORMAT;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
class DataControllerTest {

    @Mock
    DataService dataService;

    @InjectMocks
    DataController dataController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(dataController).build();
    }

    @Test
    void download() throws Exception {
        //GIVEN
        doNothing().when(dataService).downloadData();

        //WHEN
        this.mockMvc.perform(get("/download"))

            //THEN
            .andExpect(MockMvcResultMatchers.status().isAccepted());


    }

    @Test
    void presentData() throws Exception {
        //GIVEN
        Gson gson = new Gson();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        Date date = new Date();
        List<RateDTO> rates = List.of(new RateDTO("USD", BigDecimal.ONE, formatter.format(date)));

        doReturn(rates).when(dataService).getRates(formatter.format(date));

        ResultMatcher resultMatcher = MockMvcResultMatchers.content().string(gson.toJson(rates));

        //WHEN
        this.mockMvc.perform(get("/presentData").param("date", formatter.format(date)))

            //THEN
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(resultMatcher);

    }
}