package com.stockvis.service;

import com.stockvis.entity.CompanyFinancial;
import com.stockvis.entity.Stock;
import com.stockvis.repository.CompanyFinancialRepository;
import com.stockvis.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Service
public class CompanyFinancialService {

    @Autowired
    private CompanyFinancialRepository companyFinancialRepository;

    @Autowired
    private StockRepository stockRepository;

    public List<CompanyFinancial> getCompanyFinancials(String ticker) {
        Stock stock = stockRepository.findById(ticker).orElse(null);
        if (stock == null) return new ArrayList<>();
        
        String name = stock.getCompany().getName();
        List<CompanyFinancial> financials = companyFinancialRepository.findByName(name);
        
        // Only fetch new data if we don't have any records
        if (financials.isEmpty()) {
            saveCompanyFinancials(ticker);
            financials = companyFinancialRepository.findByName(name);
        }
        
        return financials;
    }

    public void saveCompanyFinancials(String ticker) {
        try {
            
            if (companyFinancialRepository.findByName(stockRepository.findById(ticker).orElse(null).getCompany().getName()).size() > 0) {
                System.out.println("Company Financials already exist for " + ticker);
                return;
            }
            Process process = executePythonScript(ticker);
            String jsonOutput = readProcessOutput(process);
              
            CompanyFinancial companyFinancial = new CompanyFinancial();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonOutput);
            JsonNode dataNode = rootNode.get("financial_data");
            String name = stockRepository.findById(ticker).orElse(null).getCompany().getName();
            companyFinancial.setName(name);
            companyFinancial.setCompany(stockRepository.findById(ticker).orElse(null).getCompany()); // Add this line

            String date = dataNode.get("date").asText();
            companyFinancial.setDate(LocalDate.parse(date));
            companyFinancial.setEbitda(dataNode.get("ebitda").asDouble());
            companyFinancial.setEarningsPerShare(dataNode.get("earningsPerShare").asDouble());
            companyFinancial.setProfitMargin(dataNode.get("profitMargin").asDouble());
            companyFinancial.setBeta(dataNode.get("beta").asDouble());
            companyFinancial.setRevenue(dataNode.get("revenue").asDouble());
            companyFinancial.setTargetPrice(dataNode.get("targetPrice").asDouble());
            // Save

            System.out.println("Company Financials: " + companyFinancial.getName() + " " + companyFinancial.getDate()
                    + " " + companyFinancial.getEbitda() + " " + companyFinancial.getEarningsPerShare() + " "
                    + companyFinancial.getProfitMargin()
                    + " " + companyFinancial.getBeta() + " " + companyFinancial.getRevenue() + " "
                    + companyFinancial.getTargetPrice());
            companyFinancialRepository.save(companyFinancial);

        } catch (Exception e) {
            System.out.println("Error: " + e);
            throw new RuntimeException("Error", e);
        }
        // Read the output of the python process
    }

    private Process executePythonScript(String ticker) throws IOException {
        String projectRoot = System.getProperty("user.dir");
        File scriptFile = new File(projectRoot, "backend/stockvis/scripts/get_company_financial_data.py")
                .getAbsoluteFile();

        if (!scriptFile.exists()) {
            throw new RuntimeException("Python script not found at: " + scriptFile.getPath());
        }

        ProcessBuilder processBuilder = new ProcessBuilder("python", scriptFile.getPath(), ticker);
        processBuilder.directory(new File(projectRoot));
        processBuilder.redirectErrorStream(true);
        return processBuilder.start();
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

    public void populateFinancialDataFromCSV() {
        String csvFile = "backend/stockvis/scripts/csvfiles/financial_data.csv";
        String line;
        String cvsSplitBy = ",";
        Set<String> existingCompanyNames = new HashSet<>();
        List<CompanyFinancial> financialsToSave = new ArrayList<>();
        int batchSize = 100;

        // First get all existing company names to avoid duplicates
        companyFinancialRepository.findAll().forEach(cf -> existingCompanyNames.add(cf.getName()));

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // Skip header
            br.readLine();
            
            int totalProcessed = 0;
            int totalSaved = 0;
            
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);
                totalProcessed++;

                if (data.length >= 8) {
                    String ticker = data[0];
                    Stock stock = stockRepository.findById(ticker).orElse(null);
                    if (stock == null) continue;

                    String companyName = stock.getCompany().getName();
                    if (existingCompanyNames.contains(companyName)) {
                        continue;  // Skip if already exists
                    }

                    CompanyFinancial financial = new CompanyFinancial();
                    financial.setName(companyName);
                    financial.setCompany(stock.getCompany());
                    financial.setDate(LocalDate.parse(data[1]));
                    financial.setEbitda(parseDoubleOrNull(data[2]));
                    financial.setEarningsPerShare(parseDoubleOrNull(data[3]));
                    financial.setProfitMargin(parseDoubleOrNull(data[4]));
                    financial.setBeta(parseDoubleOrNull(data[5]));
                    financial.setRevenue(parseDoubleOrNull(data[6]));
                    financial.setTargetPrice(parseDoubleOrNull(data[7]));

                    financialsToSave.add(financial);
                    existingCompanyNames.add(companyName); // Add to set to prevent duplicates

                    // Batch save when we reach batch size
                    if (financialsToSave.size() >= batchSize) {
                        companyFinancialRepository.saveAll(financialsToSave);
                        totalSaved += financialsToSave.size();
                        System.out.println("Saved batch of " + financialsToSave.size() + " financials. Total saved: " + totalSaved);
                        financialsToSave.clear();
                    }
                }
            }

            // Save any remaining financials
            if (!financialsToSave.isEmpty()) {
                companyFinancialRepository.saveAll(financialsToSave);
                totalSaved += financialsToSave.size();
                System.out.println("Saved final batch of " + financialsToSave.size() + " financials. Total saved: " + totalSaved);
            }

            System.out.println("Finished processing " + totalProcessed + " rows. Total saved: " + totalSaved);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to populate financial data: " + e.getMessage());
        }
    }

    public void importFinancialData() {
        String filePath = "backend/stockvis/scripts/csvfiles/financial_data.csv";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); // Skip header
            List<CompanyFinancial> financials = new ArrayList<>();
            
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String ticker = data[0];
                Stock stock = stockRepository.findById(ticker).orElse(null);
                if (stock == null) continue;

                CompanyFinancial financial = new CompanyFinancial();
                financial.setName(stock.getCompany().getName());
                financial.setCompany(stock.getCompany());
                financial.setDate(LocalDate.parse(data[1]));
                financial.setEbitda(parseDoubleOrNull(data[2]));
                financial.setEarningsPerShare(parseDoubleOrNull(data[3]));
                financial.setProfitMargin(parseDoubleOrNull(data[4]));
                financial.setBeta(parseDoubleOrNull(data[5]));
                financial.setRevenue(parseDoubleOrNull(data[6]));
                financial.setTargetPrice(parseDoubleOrNull(data[7]));
                
                financials.add(financial);
            }
            companyFinancialRepository.saveAll(financials);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Double parseDoubleOrNull(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}