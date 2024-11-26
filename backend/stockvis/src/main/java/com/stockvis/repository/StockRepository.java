package com.stockvis.repository;

import com.stockvis.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, String> {
    List<Stock> findByTicker(String ticker);

    @Query("SELECT s FROM Stock s")
    List<Stock> findTopStocksByMarketCap(int limit);
}
