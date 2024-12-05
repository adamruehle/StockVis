package com.stockvis.controller;

import com.stockvis.entity.CompanyFinancial;
import com.stockvis.entity.Dividend;
import com.stockvis.entity.EconomicData;
import com.stockvis.entity.Price;
import com.stockvis.entity.Stock;
import com.stockvis.service.DividendService;
import com.stockvis.service.MacroService;
import com.stockvis.service.PriceService;
import com.stockvis.service.StockService;
import com.stockvis.service.CompanyService;
import com.stockvis.service.CompanyFinancialService;
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

    @Autowired
    private CompanyFinancialService companyFinancialService;

    @Autowired
    private CompanyService companyService;

    @GetMapping(value = "/hello")
    public ResponseEntity<String> hello() {
        // Replace with actual logic to fetch and return stocks
        return ResponseEntity.ok("Hello World!");
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

    @GetMapping(value = "/getTopStocks")
    public ResponseEntity<List<Price>> getTopStocks(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<Price> prices = priceService.getTopMarketCaps(limit);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            // Log the exception (consider using a logger)
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping(value = "/getTopStocksByExchange")
    public ResponseEntity<List<Price>> getTopStocksByExchange(@RequestParam(defaultValue = "10") int limit,
            @RequestParam String exchange) {
        try {
            List<Price> prices = priceService.getTopMarketCapsByExchange(limit, exchange);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            // Log the exception (consider using a logger)
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping(value = "/getTopStocksBySector")
    public ResponseEntity<List<Price>> getTopStocksBySector(
            @RequestParam String sector,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Price> prices = priceService.getTopStocksBySector(limit, sector);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping(value = "/getStockPrices")
    public ResponseEntity<List<Price>> getStockPrices(@RequestParam(defaultValue = "") String ticker) {
        try {
            List<String> tickers = new ArrayList<String>();
            tickers.add(ticker);
            stockService.populatePrices(tickers, "5y", "1mo");
            List<Price> prices = priceService.getStockPrices(ticker);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            // Log the exception (consider using a logger)
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping(value = "/getDividends")
    public ResponseEntity<List<Dividend>> getDividends(@RequestParam(defaultValue = "") String ticker) {

        try {
            List<String> tickers = new ArrayList<String>();
            tickers.add(ticker);
            dividendService.populateDividends(tickers);
            List<Dividend> dividends = dividendService.getDividends(ticker);
            return ResponseEntity.ok(dividends);
        } catch (Exception e) {
            // Log the exception (consider using a logger)
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping(value = "/getCompanyFinancials")
    public ResponseEntity<List<CompanyFinancial>> getCompanyFinancials(@RequestParam(defaultValue = "") String ticker) {
        try {

            companyFinancialService.saveCompanyFinancials(ticker);
            List<CompanyFinancial> companyFinancials = companyFinancialService.getCompanyFinancials(ticker);
            return ResponseEntity.ok(companyFinancials);
        } catch (Exception e) {
            // Log the exception (consider using a logger)
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping(value = "/getEconomicData")
    public ResponseEntity<List<EconomicData>> getEconomicData() {
        try {
            List<EconomicData> economicData = macroService.getEconomicData();
            return ResponseEntity.ok(economicData);
        } catch (Exception e) {
            // Log the exception (consider using a logger)
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping(value = "/getSectors")
    public ResponseEntity<List<String>> getSectors() {
        try {
            List<String> sectors = companyService.getUniqueSectors();
            return ResponseEntity.ok(sectors);
        } catch (Exception e) {
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

    @PostMapping(value = "/populateCompanies")
    public ResponseEntity<String> populateCompanies() {
        try {
            companyService.populateCompanies();
            return ResponseEntity.ok("Companies populated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to populate companies: " + e.getMessage());
        }
    }

}
