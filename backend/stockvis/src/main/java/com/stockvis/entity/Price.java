package com.stockvis.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "price")
@IdClass(PriceId.class)
public class Price {

    @Id
    @Column(name = "ticker")
    private String ticker; // Acts as both part of the PK and a foreign key.

    @Id
    @Column(name = "date")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "ticker", referencedColumnName = "ticker", insertable = false, updatable = false)
    private Stock stock;

    @Column(name = "open_price")
    private Double openPrice;

    @Column(name = "close_price")
    private Double closePrice;

    @Column(name = "low_price")
    private Double lowPrice;

    @Column(name = "high_price")
    private Double highPrice;

    private Long volume;

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

    public Double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(Double openPrice) {
        this.openPrice = openPrice;
    }

    public Double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(Double closePrice) {
        this.closePrice = closePrice;
    }

    public Double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(Double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public Double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(Double highPrice) {
        this.highPrice = highPrice;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }
}
