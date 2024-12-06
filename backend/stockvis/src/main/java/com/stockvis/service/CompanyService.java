package com.stockvis.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.stockvis.entity.Company;
import com.stockvis.entity.Stock;
import com.stockvis.repository.CompanyRepository;
import com.stockvis.repository.StockRepository;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private StockRepository stockRepository;

    public void populateCompanies() {
        try {

            String filePath = "backend/stockvis/scripts/all_company_data.csv";
            try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
                List<String[]> rows = csvReader.readAll(); // Read all rows at once
                int counter = 0;

                for (String[] stockData : rows) {
                    if (stockData.length < 7) {
                        // Skip rows that don't have enough data
                        continue;
                    }

                    String ticker = stockData[0];
                    String sector = stockData[4];
                    String industry = stockData[5];
                    String headquarters = stockData[6];

                    Stock stock = stockRepository.findById(ticker).orElse(null);

                    if (stock == null) {
                        continue;
                    }
                    Company company = companyRepository.findById(stock.getCompany().getName()).orElse(null);

                    if (company != null) {

                        company.setSector(sector);
                        company.setIndustry(industry);
                        company.setHeadquarters(headquarters);
                        companyRepository.save(company);
                        counter++;
                    }
                }

                System.out.println("Total companies added: " + counter);
            } catch (CsvException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Company getCompanyByTicker(String ticker) {
        Stock stock = stockRepository.findById(ticker).orElse(null);
        if (stock == null) {
            return null;
        }
        return stock.getCompany();
    }

    public List<String> getUniqueSectors() {
        try {
            List<String> sectors = companyRepository.findDistinctSectors();
            return sectors != null ? sectors : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
