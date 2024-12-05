package com.stockvis.entity;

import com.stockvis.entity.Company;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "companyFinancial")
@IdClass(CompanyFinancialId.class)
public class CompanyFinancial {

    @Id
    private String name;

    @Id
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "name", referencedColumnName = "name", insertable = false, updatable = false)
    private Company company;

    private Double ebitda;
    private Double earningsPerShare;
    private Double profitMargin;
    private Double beta;
    private Double revenue;
    private Double targetPrice;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Double getEbitda() {
        return ebitda;
    }

    public void setEbitda(Double ebitda) {
        this.ebitda = ebitda;
    }

    public Double getEarningsPerShare() {
        return earningsPerShare;
    }

    public void setEarningsPerShare(Double earningsPerShare) {
        this.earningsPerShare = earningsPerShare;
    }

    public Double getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(Double profitMargin) {
        this.profitMargin = profitMargin;
    }

    public Double getBeta() {
        return beta;
    }

    public void setBeta(Double beta) {
        this.beta = beta;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }

    public Double getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(Double targetPrice) {
        this.targetPrice = targetPrice;
    }

}
