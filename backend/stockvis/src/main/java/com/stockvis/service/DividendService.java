package com.stockvis.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stockvis.entity.Dividend;
import com.stockvis.repository.DividendRepository;

@Service
public class DividendService {

    @Autowired
    private DividendRepository dividendRepository;

    public void populateDividendData() {
        try {
            String filePath = "backend/stockvis/scripts/csvfiles/all_dividends.csv";
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            reader.readLine(); // Skip the header
            String line;
            int counter = 0;

            List<Dividend> dividendList = new ArrayList<Dividend>();
            while ((line = reader.readLine()) != null) {
                String[] stockData = line.split(",");
                String ticker = stockData.length > 0 ? stockData[0] : "";
                String date = stockData.length > 1 ? stockData[1] : "";
                String dividendAmount = stockData.length > 3 ? stockData[3] : "";
                String dividendYield = stockData.length > 4 ? stockData[4] : "";

                Dividend dividend = new Dividend();

                dividend.setTicker(ticker);
                dividend.setDate(LocalDate.parse(date));
                dividend.setDividendAmount(!dividendAmount.isEmpty() ? Double.parseDouble(dividendAmount) : null);
                dividend.setDividendYield(!dividendYield.isEmpty() ? Double.parseDouble(dividendYield) : null);

                System.out.println(ticker);
                dividendRepository.save(dividend);

                counter++;
            }
            reader.close();
            System.out.println("Total stocks processed: " + counter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
