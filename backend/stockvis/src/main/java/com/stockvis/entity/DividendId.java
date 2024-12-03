package com.stockvis.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class DividendId implements Serializable {
    private String ticker;
    private LocalDate date;

    // Default constructor
    public DividendId() {
    }

    // Constructor
    public DividendId(String ticker, LocalDate date) {
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    // equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DividendId dividendId = (DividendId) o;
        return ticker.equals(dividendId.ticker) && date.equals(dividendId.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker, date);
    }
}
