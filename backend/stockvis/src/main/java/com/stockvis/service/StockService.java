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

import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.io.File;
import java.util.Iterator;
import com.fasterxml.jackson.databind.JsonNode;

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
            String tickersString = tickers.stream()
                    .map(ticker -> ticker)
                    .collect(Collectors.joining("+"));
            System.out.println(tickersString);

            // Get the project root directory
            String projectRoot = System.getProperty("user.dir");
            // Construct path to Python script
            File scriptFile = new File(projectRoot, "backend/stockvis/scripts/get_stock_prices.py").getAbsoluteFile();
            if (!scriptFile.exists()) {
                throw new RuntimeException("Python script not found at: " + scriptFile.getPath());
            }

            ProcessBuilder processBuilder = new ProcessBuilder("python3", scriptFile.getPath(), tickersString);
            // Set working directory to project root
            processBuilder.directory(new File(projectRoot));

            // Debug print command and arguments
            System.out.println("Command: " + String.join(" ", processBuilder.command()));
            System.out.println("Working directory: " + processBuilder.directory().getAbsolutePath());
            System.out.println("Tickers: " + tickersString);

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Read combined output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Python output: " + line); // Print output in real-time
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            System.out.println("Exit code: " + exitCode);
            System.out.println("Final output: " + output.toString());

            if (exitCode != 0) {
                throw new RuntimeException("Python script failed with exit code: " + exitCode);
            }

            // Parse JSON data
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(output.toString());
            List<Price> priceList = new ArrayList<>();
            int counter = 0;

            // Iterate through each field in the JSON object
            Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String ticker = entry.getKey();
                JsonNode priceData = entry.getValue();

                for (JsonNode dataPoint : priceData) {
                    String dateStr = dataPoint.get(1).asText();
                    Double price = dataPoint.get(2).asDouble();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime date = LocalDateTime.parse(dateStr, formatter);

                    // Create Price object using setters
                    Price priceEntity = new Price();
                    priceEntity.setTicker(ticker);
                    priceEntity.setDate(date);
                    priceEntity.setCurrentPrice(price);

                    priceList.add(priceEntity);
                    counter++;
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
