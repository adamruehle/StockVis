package com.stockvis.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "dividend")
@IdClass(PriceId.class)
public class Dividend {

    @Id
    @Column(name = "ticker")
    private String ticker; // Acts as both part of the PK and a foreign key.

    @Id
    @Column(name = "date")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "ticker", referencedColumnName = "ticker", insertable = false, updatable = false)
    private Stock stock;

    @Column(name = "dividend_per_share")
    private Double dividendPerShare;

    // Getters and Setters
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

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Double getDividendPerShare() {
        return dividendPerShare;
    }

    public void setDividendPerShare(Double dividendPerShare) {
        this.dividendPerShare = dividendPerShare;
    }
}
