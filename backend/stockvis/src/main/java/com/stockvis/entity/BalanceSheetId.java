package com.stockvis.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class BalanceSheetId implements Serializable {

    private Company company;  // The Company object as part of the composite key
    private Date date;        // The Date as part of the composite key

    // Default constructor
    public BalanceSheetId() {}

    // Constructor
    public BalanceSheetId(Company company, Date date) {
        this.company = company;
        this.date = date;
    }

    // Getters and Setters
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    // equals() and hashCode() to compare BalanceSheetId instances based on company and date
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BalanceSheetId that = (BalanceSheetId) o;
        return Objects.equals(company, that.company) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(company, date);
    }
}
