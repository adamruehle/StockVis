package com.stockvis.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "company")
public class Company {

    @Id
    private String name;
    private String sector;
    private String industry;
    private String headquarters;
    private Integer foundedYear;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Stock> stocks;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<CompanyFinancial> companyFinancials;

    @OneToMany(mappedBy = "companyId", cascade = CascadeType.ALL)
    private List<BalanceSheet> balanceSheets;

    @OneToMany(mappedBy = "companyId", cascade = CascadeType.ALL)
    private List<IncomeStatement> incomeStatements;
    // Getters and Setters
}
