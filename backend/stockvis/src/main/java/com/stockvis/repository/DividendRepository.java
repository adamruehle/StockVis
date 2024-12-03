package com.stockvis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stockvis.entity.Dividend;
import com.stockvis.entity.DividendId;

@Repository
public interface DividendRepository extends JpaRepository<Dividend, DividendId> {

    List<Dividend> findByTicker(String ticker);

}
