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

    public List<Price> getTopPrices(int limit) {

        Pageable pageRequest = PageRequest.of(0, limit);
        return priceRepository.findTopStocksByMarketCap(pageRequest);
    }
}
