package com.stockvis.service;

import com.stockvis.entity.PriceId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
public class PriceService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private PriceRepository priceRepository;
    @Autowired
    private CompanyRepository companyRepository;

    public void populateMarketCaps() {
        try {
            String filePath = "backend/scripts/market_caps.csv";
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            reader.readLine(); // Skip the header
            String line;
            int counter = 0;

            while ((line = reader.readLine()) != null) {
                String[] stockData = line.split(",");
                String ticker = stockData[0];
                String marketCap = stockData[1];

                List<Price> prices = priceRepository.findByTicker(ticker);
                // Create and save the Stock entity
                if (prices.size() == 0) {
                    continue;
                }
                Price price = prices.get(0);
                if (marketCap.equals("N/A")) {
                    price.setMarketCap(null);
                } else {
                    price.setMarketCap(Double.parseDouble(marketCap));
                }
                priceRepository.save(price);
                counter++;
            }
            reader.close();
            System.out.println("Total stocks processed: " + counter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Price> getTopMarketCaps(int limit) {

        Pageable pageRequest = PageRequest.of(0, limit);
        return priceRepository.findTopStocksByMarketCap(pageRequest);
    }

    public List<Price> getTopMarketCapsByExchange(int limit, String exchange) {

        Pageable pageRequest = PageRequest.of(0, limit);
        return priceRepository.findTopStocksByMarketCap(pageRequest, exchange);
    }

    public List<Price> getTopMarketCapsBySector(int limit, String sector) {

        Pageable pageRequest = PageRequest.of(0, limit);
        return priceRepository.findTopStocksByMarketCap(pageRequest, sector);
    }

    public List<Price> getStockPrices(String ticker) {
        return priceRepository.findByTicker(ticker);
    }

    public List<Price> getTopStocksBySector(int limit, String sector) {
        Pageable pageRequest = PageRequest.of(0, limit);
        List<Price> results = priceRepository.findTopStocksBySector(pageRequest, sector);
        return results;
    }

}
