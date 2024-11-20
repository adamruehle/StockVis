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
    @JoinColumn(name = "name", nullable = false)
    private Company company;

    private Double earningsPerShare;
    private Double liabilities;
    private Double assets;
    private Double netIncome;
    private Double revenue;

    // Getters and Setters
}
