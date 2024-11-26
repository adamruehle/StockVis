package com.stockvis.controller;

import com.stockvis.entity.Price;
import com.stockvis.entity.Stock;
import com.stockvis.service.PriceService;
import com.stockvis.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * StockVisController handles API requests for stock-related operations.
 */
@RestController
@CrossOrigin(origins = "https://localhost:3000")
@RequestMapping("/api")
public class StockVisController {

    @Autowired
    private StockService stockService;

    @Autowired
    private PriceService priceService;
    @GetMapping(value = "/hello")
    public ResponseEntity<String> hello() {
        // Replace with actual logic to fetch and return stocks
        return ResponseEntity.ok("Hello World!");
    }

    @GetMapping(value = "/getTopPrices")
    public ResponseEntity<List<Price>> getTopPrices(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<Price> prices = priceService.getTopPrices(limit);
            return ResponseEntity.ok(prices);
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
    public ResponseEntity<String> populatePrices() {
        try {
            stockService.populatePrices();
            return ResponseEntity.ok("Stock Prices populated successfully!");
        } catch (Exception e) {
            // Log the exception (consider using a logger)
            return ResponseEntity.status(500).body("Failed to populate stocks prices: " + e.getMessage());
        }
    }


}