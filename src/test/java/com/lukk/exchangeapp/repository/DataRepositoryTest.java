package com.lukk.exchangeapp.repository;

import com.lukk.exchangeapp.entity.Rate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
class DataRepositoryTest {

    @Autowired
    DataRepository dataRepository;

    @Test
    void findAllByDateBefore() {
        //GIVEN
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.MONTH, -1);
        Date monthAgo = cal.getTime();

        cal.add(Calendar.WEEK_OF_MONTH, 3);
        Date weekAgo = cal.getTime();

        cal.add(Calendar.WEEK_OF_MONTH, 3);

        Rate expected = new Rate("USD", BigDecimal.ONE, monthAgo);
        Rate additional = new Rate("USD", BigDecimal.ONE, cal.getTime());

        dataRepository.saveAll(List.of(expected, additional));

        //WHEN
        List<Rate> actual = dataRepository.findAllByDateBefore(weekAgo);

        //THEN
        assertEquals(1, actual.size());
        assertEquals(expected, actual.get(0));
    }

    @Test
    void findAllByDateBefore_whenNoRates() {
        //GIVEN
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.WEEK_OF_MONTH, -1);
        Date weekAgo = cal.getTime();


        //WHEN
        List<Rate> actual = dataRepository.findAllByDateBefore(weekAgo);

        //THEN
        assertEquals(0, actual.size());
    }

    @Test
    void findFirstByDateEquals() {
        //GIVEN
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.MONTH, -1);
        Date monthAgo = cal.getTime();

        cal.add(Calendar.WEEK_OF_MONTH, 3);

        Rate expected = new Rate("USD", BigDecimal.ONE, monthAgo);
        Rate additional = new Rate("USD", BigDecimal.ONE, cal.getTime());

        dataRepository.saveAll(List.of(expected, additional));

        //WHEN
        Rate actual = dataRepository.findFirstByDateEquals(monthAgo);

        //THEN
        assertEquals(expected, actual);
    }

    @Test
    void findFirstByDateEquals_whenNoRates() {
        //GIVEN
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.MONTH, -1);
        Date monthAgo = cal.getTime();

        //WHEN
        Rate actual = dataRepository.findFirstByDateEquals(monthAgo);

        //THEN
        assertNull(actual);
    }

    @Test
    void findAllByDate() {
        //GIVEN
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.MONTH, -1);
        Date monthAgo = cal.getTime();

        cal.add(Calendar.WEEK_OF_MONTH, 3);
        Date weekAgo = cal.getTime();

        cal.add(Calendar.WEEK_OF_MONTH, 3);

        Rate expected = new Rate("USD", BigDecimal.ONE, monthAgo);
        Rate secondExpected = new Rate("USD", BigDecimal.TEN, monthAgo);
        Rate additional = new Rate("USD", BigDecimal.ONE, cal.getTime());

        dataRepository.saveAll(List.of(expected, secondExpected, additional));

        //WHEN
        List<Rate> actual = dataRepository.findAllByDateBefore(weekAgo);

        //THEN
        assertEquals(2, actual.size());
    }

    @Test
    void findAllByDate_whenNoRates() {
        //GIVEN
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.WEEK_OF_MONTH, -1);
        Date weekAgo = cal.getTime();

        //WHEN
        List<Rate> actual = dataRepository.findAllByDateBefore(weekAgo);

        //THEN
        assertEquals(0, actual.size());
    }

    @Test
    void findAllByDateAfterAndDateBefore() {
        //GIVEN
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.MONTH, -1);
        Date monthAgo = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date almostMonthAgo = cal.getTime();

        cal.add(Calendar.WEEK_OF_MONTH, 1);
        Date threeWeeksAgo = cal.getTime();

        cal.add(Calendar.WEEK_OF_MONTH, 2);
        Date weekAgo = cal.getTime();

        cal.add(Calendar.WEEK_OF_MONTH, 3);

        Rate expected = new Rate("USD", BigDecimal.ONE, almostMonthAgo);
        Rate secondExpected = new Rate("USD", BigDecimal.TEN, threeWeeksAgo);
        Rate additional = new Rate("USD", BigDecimal.ONE, cal.getTime());

        dataRepository.saveAll(List.of(expected, secondExpected, additional));

        //WHEN
        List<Rate> actual = dataRepository.findAllByDateAfterAndDateBefore(monthAgo, weekAgo);

        //THEN
        assertEquals(2, actual.size());
    }

    @Test
    void findAllByDateAfterAndDateBefore_whenNoRates() {
        //GIVEN
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.MONTH, -1);
        Date monthAgo = cal.getTime();

        cal.add(Calendar.WEEK_OF_MONTH, 3);
        Date weekAgo = cal.getTime();

        //WHEN
        List<Rate> actual = dataRepository.findAllByDateAfterAndDateBefore(monthAgo, weekAgo);

        //THEN
        assertEquals(0, actual.size());
    }
}