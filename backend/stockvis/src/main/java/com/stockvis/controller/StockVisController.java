package com.stockvis.controller;

import com.stockvis.entity.Price;
import com.stockvis.entity.Stock;
import com.stockvis.service.DividendService;
import com.stockvis.service.MacroService;
import com.stockvis.service.PriceService;
import com.stockvis.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * StockVisController handles API requests for stock-related operations.
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class StockVisController {

    @Autowired
    private StockService stockService;

    @Autowired
    private PriceService priceService;

    @Autowired
    private MacroService macroService;

    @Autowired
    private DividendService dividendService;

    @GetMapping(value = "/hello")
    public ResponseEntity<String> hello() {
        // Replace with actual logic to fetch and return stocks
        return ResponseEntity.ok("Hello World!");
    }

    @GetMapping(value = "/getTopMarketCaps")
    public ResponseEntity<List<Price>> getTopPrices(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<Price> prices = priceService.getTopMarketCaps(limit);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            // Log the exception (consider using a logger)
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping(value = "/getStocks")
    public ResponseEntity<List<Stock>> getStocks(@RequestParam(defaultValue = "") String tickerString) {
        try {
            List<Stock> stocks = stockService.getStocks(tickerString);
            return ResponseEntity.ok(stocks);
        } catch (Exception e) {
            // Log the exception (consider using a logger)
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping(value = "/populateStocks")
    public ResponseEntity<String> populateStocks() {
        try {
            stockService.populateStocks();
            return ResponseEntity.ok("Stocks populated successfully!");
        } catch (Exception e) {
            // Log the exception (consider using a logger)
            return ResponseEntity.status(500).body("Failed to populate stocks: " + e.getMessage());
        }
    }

    @PostMapping(value = "/populatePrices")
    public ResponseEntity<String> populatePrices(@RequestParam List<String> tickers,
            @RequestParam List<String> dateRange, @RequestParam String interval) {
        try {
            stockService.populatePrices(tickers, "1d", "1d");
            return ResponseEntity.ok("Stock Prices populated successfully!");
        } catch (Exception e) {
            // Log the exception (consider using a logger)
            return ResponseEntity.status(500).body("Failed to populate stocks prices: " + e.getMessage());
        }
    }

    @PostMapping(value = "/populateTopPrices")
    public ResponseEntity<String> populateTopPrices(@RequestParam(defaultValue = "100") int limit) {
        try {
            List<Price> tickers = priceService.getTopMarketCaps(limit);

            List<String> tickerStrings = new ArrayList<String>();
            for (Price price : tickers) {
                tickerStrings.add(price.getTicker());
            }
            stockService.populatePrices(tickerStrings, "1d", "30");
            return ResponseEntity.ok("Top " + limit + " stock prices populated successfully!");
        } catch (Exception e) {
            // Log the exception (consider using a logger)
            return ResponseEntity.status(500).body("Failed to populate top stock prices: " + e.getMessage());
        }
    }

    @PostMapping(value = "/populateMacroData")
    public ResponseEntity<String> populateMacroData() {
        try {
            macroService.populateMacroData();
            return ResponseEntity.ok("Macro Data populated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to populate macro data: " + e.getMessage());
        }
    }

    @PostMapping(value = "/populateDividendData")
    public ResponseEntity<String> populateDividendData() {
        try {
            dividendService.populateDividendData();
            return ResponseEntity.ok("Dividend Data populated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to populate dividend data: " + e.getMessage());
        }
    }

    @PostMapping(value = "/populateMarketCaps")
    public ResponseEntity<String> populateMarketCaps() {
        try {
            priceService.populateMarketCaps();
            return ResponseEntity.ok("Market Caps populated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to populate stocks prices: " + e.getMessage());
        }
    }

    }

    
    
        
    

        

    