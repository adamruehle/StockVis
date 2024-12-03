package com.stockvis.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "dividend")
@IdClass(DividendId.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Dividend {

    @Id
    @Column(name = "ticker")
    private String ticker;

    @Id
    @Column(name = "date")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "ticker", referencedColumnName = "ticker", insertable = false, updatable = false)
    private Stock stock;

    private double dividendAmount;

    private double dividendYield;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getDividendAmount() {
        return dividendAmount;
    }

    public void setDividendAmount(double dividendAmount) {
        this.dividendAmount = dividendAmount;
    }

    public double getDividendYield() {
        return dividendYield;
    }

    public void setDividendYield(double dividendYield) {
        this.dividendYield = dividendYield;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

}
