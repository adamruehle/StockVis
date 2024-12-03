package com.stockvis.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "economicData")
public class EconomicData {

    @Id
    private LocalDate date;
    private Double interestRate;
    private Double inflationRate;
    private Double unemploymentRate;
    private Double gdp;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public Double getInflationRate() {
        return inflationRate;
    }

    public void setInflationRate(Double inflationRate) {
        this.inflationRate = inflationRate;
    }

    public Double getUnemploymentRate() {
        return unemploymentRate;
    }

    public void setUnemploymentRate(Double unemploymentRate) {
        this.unemploymentRate = unemploymentRate;
    }

    public Double getGdp() {
        return gdp;
    }

    public void setGdp(Double gdp) {
        this.gdp = gdp;
    }

    // Getters and Setters
}
