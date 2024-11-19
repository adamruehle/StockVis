package com.stockvis.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "stock")
public class Stock {

    @Id
    private String ticker;
    private String companyName;



    private String exchange;

    @ManyToOne
    @JoinColumn(name = "companyId", nullable = false)
    private Company company;

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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }
}