package com.stockvis.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.stockvis.entity.Price;
import com.stockvis.entity.PriceId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PriceRepository extends JpaRepository<Price, PriceId> {
    List<Price> findByTicker(String ticker);

    @Query("SELECT p.id FROM Price p WHERE p.ticker IN :tickers")
    List<PriceId> findPriceIdsByTickers(@Param("tickers") List<String> tickers);

    @Query("""
        SELECT p FROM Price p
        JOIN Stock s ON s.ticker = p.ticker
        WHERE p.date = (
            SELECT MAX(p2.date) FROM Price p2 WHERE p2.ticker = s.ticker
        )
        ORDER BY p.marketCap DESC
    """)
    List<Price> findTopStocksByMarketCap(Pageable pageable);
}
