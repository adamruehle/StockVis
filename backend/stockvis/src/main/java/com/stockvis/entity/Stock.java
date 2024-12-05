package com.stockvis.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "stock")
public class Stock {

    @Id
    private String ticker;

    @ManyToOne
    @JoinColumn(name = "company_name", referencedColumnName = "name", nullable = false)
    private Company company;

    private String exchange;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL)
    private List<Price> prices;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL)
    private List<Dividend> dividends;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }
}