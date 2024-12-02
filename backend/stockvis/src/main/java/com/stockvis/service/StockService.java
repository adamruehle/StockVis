package com.stockvis.service;

import com.stockvis.entity.PriceId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockvis.entity.Company;
import com.stockvis.entity.Price;
import com.stockvis.entity.Stock;
import com.stockvis.repository.CompanyRepository;
import com.stockvis.repository.PriceRepository;
import com.stockvis.repository.StockRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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

    public void populatePrices(List<String> tickers) {
        try {
            // Prepare the command to call the Python script with tickers as arguments
            List<String> command = new ArrayList<>();
            command.add("python3");
            command.add("scripts/get_stock_prices.py");
            command.addAll(tickers);

            // Call Python script to retrieve stock prices
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            // Read JSON data from Python script's stdout
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            process.waitFor(); // Wait for the script to finish

            // Parse JSON data
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> stockData = objectMapper.readValue(output.toString(),
                    new TypeReference<List<Map<String, Object>>>() {});

            List<Price> priceList = new ArrayList<>();
            int counter = 0;

            // Fetch existing PriceIds for the provided tickers
            Set<PriceId> existingPriceIds = new HashSet<>(priceRepository.findPriceIdsByTickers(tickers));

            // Process each stock data entry
            for (Map<String, Object> stock : stockData) {
                String ticker = (String) stock.get("Ticker");
                String time = (String) stock.get("Datetime");
                Double value = Double.parseDouble(stock.get("Price").toString());

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime date = LocalDateTime.parse(time, formatter);

                PriceId priceId = new PriceId(ticker, date);

                // Check if the record already exists
                if (!existingPriceIds.contains(priceId)) {
                    // Create the Price entity
                    Price price = new Price();
                    price.setTicker(ticker);
                    price.setDate(date);
                    price.setCurrentPrice(value);

                    priceList.add(price);
                    counter++;
                }

                // Batch save after every 1000 records
                if (priceList.size() >= 1000) {
                    priceRepository.saveAll(priceList);
                    priceList.clear();
                }
            }

            // Save any remaining records
            if (!priceList.isEmpty()) {
                priceRepository.saveAll(priceList);
            }

            System.out.println("Total prices processed: " + counter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
