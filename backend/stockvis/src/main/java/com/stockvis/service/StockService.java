package com.stockvis.service;

import com.stockvis.entity.Stock;
import com.stockvis.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    public void populateStocks() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python3", "stockvis/scripts/get_all_stocks.py");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            List<Stock> stocks = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                String[] stockData = line.split(",");
                Stock stock = new Stock();
                stock.setTicker(stockData[0]);
                stock.setCompanyName(stockData[1]);
                stock.setExchange(stockData[2]);
                stocks.add(stock);
            }

            stockRepository.saveAll(stocks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
