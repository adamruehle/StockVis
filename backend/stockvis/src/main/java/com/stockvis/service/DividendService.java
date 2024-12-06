package com.stockvis.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.stockvis.entity.Dividend;
import com.stockvis.entity.Stock;
import com.stockvis.repository.DividendRepository;
import com.stockvis.repository.StockRepository;

@Service
public class DividendService {

    @Autowired
    private DividendRepository dividendRepository;

    @Autowired
    private StockRepository stockRepository;

    public List<Dividend> parseDividendData(String json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Dividend.class));
    }

    public void populateDividends(List<String> tickers) {
        try {

            Process process = executePythonScript(tickers);
            String jsonOutput = readProcessOutput(process);
            System.out.println("JSON output: " + jsonOutput);
            // Parse JSON to match array structure
            ObjectMapper mapper = new ObjectMapper();
            List<Dividend> dividends = parseDividendData(jsonOutput);

            for (Dividend dividend : dividends) {

                Stock stock = stockRepository.findById(dividend.getTicker()).orElse(null);
                dividend.setStock(stock);
            }
            dividendRepository.saveAll(dividends);

            if (dividends.size() > 0) {
                System.out.println("Saved " + dividends.size() + " dividends");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

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

    public List<Dividend> getDividends(String ticker) {
        return dividendRepository.findByTicker(ticker);
    }

    private Process executePythonScript(List<String> tickers) throws IOException {
        String tickersString = String.join("+", tickers);
        String projectRoot = System.getProperty("user.dir");
        File scriptFile = new File(projectRoot, "backend/stockvis/scripts/get_dividend_data.py").getAbsoluteFile();

        if (!scriptFile.exists()) {
            throw new RuntimeException("Python script not found at: " + scriptFile.getPath());
        }

        // Try different Python commands
        String[] possiblePythonCommands = {"python", "python3", "py"};
        
        for (String pythonCommand : possiblePythonCommands) {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(pythonCommand, scriptFile.getPath(), tickersString);
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
}
