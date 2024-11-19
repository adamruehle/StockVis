package com.stockvis.controller;

import com.stockvis.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

/**
 * StockVisController handles API requests for stock-related operations.
 */
@RestController
@RequestMapping("/api")
public class StockVisController {

    @Autowired
    private StockService stockService;

    /**
     * Endpoint to retrieve available stocks.
     *
     * @return A sample response string (replace with actual implementation).
     */
    @GetMapping(value = "/getStocks")
    public ResponseEntity<String> getStocks() {
        // Replace with actual logic to fetch and return stocks
        return ResponseEntity.ok("Hello World!");
    }

    /**
     * Endpoint to populate stocks.
     *
     * @return A success message if stocks are populated successfully.
     */
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
}