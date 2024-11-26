package com.stockvis.repository;

import com.stockvis.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, String> {
    List<Stock> findByTicker(String ticker);

    @Query("SELECT s FROM Stock s WHERE s.ticker LIKE %:tickerString%")
    List<Stock> findByTickerContaining(@Param("tickerString") String tickerString);

}
