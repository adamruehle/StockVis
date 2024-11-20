package com.stockvis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.stockvis.entity.Company;
import com.stockvis.entity.Stock;
import com.stockvis.repository.CompanyRepository;
import com.stockvis.repository.StockRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public void populateStocks() {
        try {
            // Call Python script to retrieve stocks
            ProcessBuilder processBuilder = new ProcessBuilder("python3", "stockvis/scripts/get_all_stocks.py");
            Process process = processBuilder.start();
            process.waitFor();  // Wait for the script to finish

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
}
