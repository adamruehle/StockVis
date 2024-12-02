package com.stockvis.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class PriceId implements Serializable {

    private String ticker;
    private LocalDateTime date;

    // Default constructor
    public PriceId() {}

    // Constructor
    public PriceId(String ticker, LocalDateTime date) {
        this.ticker = ticker;
        this.date = date;
    }

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

    // equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriceId priceId = (PriceId) o;
        return ticker.equals(priceId.ticker) && date.equals(priceId.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker, date);
    }
}
