package com.stockvis.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stockvis.entity.EconomicData;
import com.stockvis.repository.EconomicDataRepository;

@Service
public class MacroService {

    @Autowired
    private EconomicDataRepository economicDataRepository;

    public void populateMacroData() {
        try {
            String filePath = "backend/scripts/macro_data.csv";
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            reader.readLine(); // Skip the header
            String line;
            int counter = 0;

            List<EconomicData> economicDataList = new ArrayList<EconomicData>();
            while ((line = reader.readLine()) != null) {
                String[] stockData = line.split(",");
                String date = stockData.length > 0 ? stockData[0] : "";
                String gdp = stockData.length > 1 ? stockData[1] : "";
                String unemploymentRate = stockData.length > 2 ? stockData[2] : "";
                String interestRate = stockData.length > 3 ? stockData[3] : "";
                String inflationRate = stockData.length > 4 ? stockData[4] : "";

                EconomicData data = new EconomicData();

                data.setDate(LocalDate.parse(date));
                data.setGdp(!gdp.isEmpty() ? Double.parseDouble(gdp) : null);
                data.setUnemploymentRate(!unemploymentRate.isEmpty() ? Double.parseDouble(unemploymentRate) : null);
                data.setInterestRate(!interestRate.isEmpty() ? Double.parseDouble(interestRate) : null);
                data.setInflationRate(!inflationRate.isEmpty() ? Double.parseDouble(inflationRate) : null);

                economicDataList.add(data);

                counter++;
            }
            economicDataRepository.saveAll(economicDataList);
            reader.close();
            System.out.println("Total stocks processed: " + counter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<EconomicData> getEconomicData() {
        return economicDataRepository.findAll();
    }
}
