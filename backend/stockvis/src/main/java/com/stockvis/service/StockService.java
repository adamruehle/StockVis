package com.stockvis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockvis.entity.Company;
import com.stockvis.entity.Price;
import com.stockvis.entity.Stock;
import com.stockvis.repository.CompanyRepository;
import com.stockvis.repository.PriceRepository;
import com.stockvis.repository.StockRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.io.File;
import org.springframework.transaction.annotation.Transactional;

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
            String projectRoot = System.getProperty("user.dir");
            File scriptFile = new File(projectRoot, "stockvis/scripts/get_all_stocks.py").getAbsoluteFile();
            
            // Try different Python commands
            String[] possiblePythonCommands = {"python", "python3", "py"};
            Process process = null;
            
            for (String pythonCommand : possiblePythonCommands) {
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder(pythonCommand, scriptFile.getPath());
                    processBuilder.directory(new File(projectRoot));
                    process = processBuilder.start();
                    break;
                } catch (IOException e) {
                    continue;
                }
            }
            
            if (process == null) {
                throw new RuntimeException("Python not found. Please ensure Python is installed and in your system PATH.");
            }

            process.waitFor();
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

    // First create DTO for JSON structure
    private static class StockPriceResponse {
        public List<List<Object>> stocks; // Array of [ticker, datetime, price]
    }

    @Transactional
    public void populatePrices(List<String> tickers, String range, String interval) {
        try {

            Process process = executePythonScript(tickers, range, interval);
            String jsonOutput = readProcessOutput(process);
            System.out.println("JSON output: " + jsonOutput);
            // Parse JSON to match array structure
            ObjectMapper mapper = new ObjectMapper();
            StockPriceResponse response = mapper.readValue(jsonOutput, StockPriceResponse.class);
            for (List<Object> stockData : response.stocks) {
                Price price = new Price();
                price.setTicker((String) stockData.get(0));
                price.setDate(LocalDateTime.parse((String) stockData.get(1),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                price.setCurrentPrice(((Number) stockData.get(2)).doubleValue());
                price.setStock(stockRepository.findById(price.getTicker()).orElse(null));
                System.out.println(price.getTicker() + " " + price.getDate() + " " + price.getCurrentPrice());
                priceRepository.save(price);
            }

            if (response.stocks.size() > 0) {
                System.out.println("Saved " + response.stocks.size() + " prices");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Process executePythonScript(List<String> tickers, String range, String interval) throws IOException {
        String tickersString = String.join("+", tickers);
        String projectRoot = System.getProperty("user.dir");
        File scriptFile = new File(projectRoot, "backend/stockvis/scripts/get_stock_prices.py").getAbsoluteFile();

        if (!scriptFile.exists()) {
            throw new RuntimeException("Python script not found at: " + scriptFile.getPath());
        }

        // Try different Python commands
        String[] possiblePythonCommands = {"python", "python3", "py"};
        ProcessBuilder processBuilder = null;
        
        for (String pythonCommand : possiblePythonCommands) {
            try {
                processBuilder = new ProcessBuilder(pythonCommand, scriptFile.getPath(), range, interval, tickersString);
                processBuilder.directory(new File(projectRoot));
                processBuilder.redirectErrorStream(true);
                return processBuilder.start();
            } catch (IOException e) {
                continue;
            }
        }

        // If none of the commands worked
        throw new RuntimeException("Python not found. Please ensure Python is installed and in your system PATH.");
    }

    private String readProcessOutput(Process process) throws IOException, InterruptedException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            System.out.println("Python output: " + line);
            output.append(line).append("\n");
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Python script failed with exit code: " + exitCode);
        }

        return output.toString();
    }

    public List<Stock> getStocks(String tickerString) {
        Set<Stock> stocks = new LinkedHashSet<>();
        if (tickerString.isEmpty()) {
            return stockRepository.findAll();
        } else {

            stocks.addAll(stockRepository.findByTickerContaining(tickerString));
            stocks.addAll(stockRepository.findByCompanyContaining(tickerString));
            return new ArrayList<>(stocks);
        }
    }
}
