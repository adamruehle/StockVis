package com.stockvis.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class CompanyFinancialId implements Serializable {

    private String name;
    private LocalDate date;

    // Default constructor
    public CompanyFinancialId() {}

    // Constructor
    public CompanyFinancialId(String name, LocalDate date) {
        this.name = name;
        this.date = date;
    }

    // Getters and Setters
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

    // equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyFinancialId that = (CompanyFinancialId) o;
        return name.equals(that.name) && date.equals(that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, date);
    }
}
