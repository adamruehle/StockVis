package com.stockvis.service;

import com.stockvis.entity.PriceId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.stockvis.entity.Company;
import com.stockvis.entity.Price;
import com.stockvis.entity.Stock;
import com.stockvis.repository.CompanyRepository;
import com.stockvis.repository.PriceRepository;
import com.stockvis.repository.StockRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private PriceRepository priceRepository;
    @Autowired
    private CompanyRepository companyRepository;

    public void populateStocks() {
        try {
            // Call Python script to retrieve stocks
            ProcessBuilder processBuilder = new ProcessBuilder("python3", "stockvis/scripts/get_all_stocks.py");
            Process process = processBuilder.start();
            process.waitFor(); // Wait for the script to finish

            // Now process the generated CSV
            String filePath = "stockvis/scripts/all_stocks.csv";
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            int counter = 0;

            while ((line = reader.readLine()) != null) {
                String[] stockData = line.split(",");
                String ticker = stockData[0];
                String companyName = stockData[1];
                String exchange = stockData[2];

                // Check if the Company exists in the database, if not, create it
                Company company = companyRepository.findById(companyName).orElseGet(() -> {
                    Company newCompany = new Company();
                    newCompany.setName(companyName);
                    // Optionally, set other company fields here if necessary
                    companyRepository.save(newCompany);
                    return newCompany;
                });

                // Create and save the Stock entity
                Stock stock = new Stock();
                stock.setTicker(ticker);
                stock.setExchange(exchange);
                stock.setCompany(company);
                stockRepository.save(stock);
                counter++;
            }

            System.out.println("Total stocks processed: " + counter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Stock> getTopStocks(int limit) {
        return stockRepository.findByTicker("AAPL");
    }

    public void populatePrices() {
        try {
//            // Call Python script to retrieve stocks
//            ProcessBuilder processBuilder = new ProcessBuilder("python3", "scripts/get_stock_data.py");
//            Process process = processBuilder.start();
//            process.waitFor(); // Wait for the script to finish

            // Now process the generated CSV
            String filePath = "stockvis/scripts/stock_prices.csv";
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            reader.readLine(); // Skip the header
            String line;
            int counter = 0;

            while ((line = reader.readLine()) != null) {
                String[] stockData = line.split(",");
                String ticker = stockData[0];
                String time = stockData[1];
                String value = stockData[2];

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime date = LocalDateTime.parse(time, formatter);

                // Create and save the Stock entity
                Price price = new Price();
                price.setTicker(ticker);
                price.setDate(date);
                price.setCurrentPrice(Double.parseDouble(value));

                if (priceRepository.findById(new PriceId(ticker, date)).isPresent()) {
                    continue;
                }

                priceRepository.save(price);
                counter++;
            }

            System.out.println("Total stocks processed: " + counter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
