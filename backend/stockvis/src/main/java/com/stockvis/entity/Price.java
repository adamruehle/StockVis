package com.stockvis.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "price")
@IdClass(PriceId.class)
public class Price {

    @Id
    @Column(name = "ticker")
    private String ticker; // Acts as both part of the PK and a foreign key.

    @Id
    @Column(name = "date")
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "ticker", referencedColumnName = "ticker", insertable = false, updatable = false)
    private Stock stock;

    @Column(name = "current_price")
    private Double currentPrice;

    private Double marketCap;
    private Long volume;

    // Getters and Setters
    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }
}
