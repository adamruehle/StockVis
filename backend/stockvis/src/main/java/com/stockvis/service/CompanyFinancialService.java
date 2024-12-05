package com.stockvis.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.stockvis.entity.CompanyFinancial;
import com.stockvis.repository.CompanyFinancialRepository;
import com.stockvis.repository.StockRepository;

@Service
public class CompanyFinancialService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private CompanyFinancialRepository CompanyFinancialRepository;

    public List<CompanyFinancial> getCompanyFinancials(String ticker) {

        String name = stockRepository.findById(ticker).orElse(null).getCompany().getName();
        return CompanyFinancialRepository.findByName(name);
    }

    public void saveCompanyFinancials(String ticker) {
        try {
            
            if (CompanyFinancialRepository.findByName(stockRepository.findById(ticker).orElse(null).getCompany().getName()).size() > 0) {
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
            CompanyFinancialRepository.save(companyFinancial);

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

        ProcessBuilder processBuilder = new ProcessBuilder("python3", scriptFile.getPath(), ticker);
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
}