package com.stockvis.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.stockvis.entity.Price;
import com.stockvis.entity.PriceId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceRepository extends JpaRepository<Price, PriceId> {
    List<Price> findByTicker(String ticker);

    @Query("SELECT p.id FROM Price p WHERE p.ticker IN :tickers")
    List<PriceId> findPriceIdsByTickers(@Param("tickers") List<String> tickers);

    @Query("""
                SELECT p FROM Price p
                JOIN Stock s ON s.ticker = p.ticker
                WHERE p.date = (
                    SELECT MIN(p2.date) FROM Price p2 WHERE p2.ticker = s.ticker
                )
                ORDER BY p.marketCap DESC
            """)
    List<Price> findTopStocksByMarketCap(Pageable pageRequest);

    @Query("""
                SELECT p FROM Price p
                JOIN Stock s ON s.ticker = p.ticker
                WHERE p.date = (
                    SELECT MIN(p2.date) FROM Price p2 WHERE p2.ticker = s.ticker
                )
                AND s.exchange = :exchange
                ORDER BY p.marketCap DESC
            """)
    List<Price> findTopStocksByMarketCap(Pageable pageRequest, @Param("exchange") String exchange);

    @Query("""
            SELECT p
            FROM Price p
            JOIN p.stock s
            JOIN s.company c
            WHERE p.date = (
                SELECT MAX(p2.date)
                FROM Price p2
                WHERE p2.stock = s
            )
            AND c.sector = :sector
            AND s IN (
                SELECT p3.stock
                FROM Price p3
                WHERE p3.marketCap IS NOT NULL
            )
            ORDER BY (
                SELECT p4.marketCap
                FROM Price p4
                WHERE p4.stock = s
                  AND p4.marketCap IS NOT NULL
                ORDER BY p4.marketCap DESC
                LIMIT 1
            ) DESC
            """)
    List<Price> findTopStocksBySector(Pageable pageRequest, @Param("sector") String sector);
}
