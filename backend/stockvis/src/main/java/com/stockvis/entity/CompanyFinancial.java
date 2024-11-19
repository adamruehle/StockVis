package com.stockvis.entity;

import com.stockvis.entity.Company;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "companyFinancial")
public class CompanyFinancial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long financialId;

    @ManyToOne
    @JoinColumn(name = "companyId", nullable = false)
    private Company company;

    private LocalDate date;
    private Double earningsPerShare;
    private Double liabilities;
    private Double assets;
    private Double netIncome;
    private Double revenue;

    // Getters and Setters
}
