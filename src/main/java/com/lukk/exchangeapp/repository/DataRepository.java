package com.lukk.exchangeapp.repository;

import com.lukk.exchangeapp.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DataRepository extends JpaRepository<Rate, Long> {

    List<Rate> findAllByDateBefore(Date olderThan);

    Rate findFirstByDateEquals(Date date);

    List<Rate> findAllByDate(Date date);

    List<Rate> findAllByDateAfterAndDateBefore(Date start, Date end);
}
