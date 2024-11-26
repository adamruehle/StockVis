package com.stockvis.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stockvis.entity.Price;
import com.stockvis.entity.PriceId;

import java.util.List;

public interface PriceRepository extends JpaRepository<Price, PriceId> {
    List<Price> findByTicker(String ticker);
}
