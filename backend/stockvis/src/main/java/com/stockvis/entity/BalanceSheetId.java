package com.stockvis.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class BalanceSheetId implements Serializable {
    private String companyId;
    private Date date;

    // Default constructor
    public BalanceSheetId() {}

    public BalanceSheetId(String companyId, Date date) {
        this.companyId = companyId;
        this.date = date;
    }

    // Getters and Setters
    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    // equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BalanceSheetId that = (BalanceSheetId) o;
        return Objects.equals(companyId, that.companyId) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyId, date);
    }
}
