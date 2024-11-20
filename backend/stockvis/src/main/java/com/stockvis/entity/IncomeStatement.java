package com.stockvis.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@IdClass(BalanceSheetId.class)
public class IncomeStatement implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "company_name", referencedColumnName = "name", nullable = false)
    private Company company;

    @Id
    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    private Date date;

    private BigDecimal totalRevenue;
    private BigDecimal costOfRevenue;
    private BigDecimal grossProfit;
    private BigDecimal operatingExpense;
    private BigDecimal operatingIncome;
    private BigDecimal netNonOperatingInterestIncomeExpense;
    private BigDecimal otherIncomeExpense;
    private BigDecimal pretaxIncome;
    private BigDecimal taxProvision;
    private BigDecimal netIncomeCommonStockholders;
    private BigDecimal dilutedNiAvailableToComStockholders;
    private BigDecimal basicEps;
    private BigDecimal dilutedEps;
    private BigDecimal basicAverageShares;
    private BigDecimal dilutedAverageShares;
    private BigDecimal totalOperatingIncomeAsReported;
    private BigDecimal totalExpenses;
    private BigDecimal netIncomeFromContinuingAndDiscontinuedOperation;
    private BigDecimal normalizedIncome;
    private BigDecimal interestIncome;
    private BigDecimal interestExpense;
    private BigDecimal netInterestIncome;
    private BigDecimal ebit;
    private BigDecimal ebitda;
    private BigDecimal reconciledCostOfRevenue;
    private BigDecimal reconciledDepreciation;
    private BigDecimal netIncomeFromContinuingOperationNetMinorityInterest;
    private BigDecimal normalizedEbitda;
    private BigDecimal taxRateForCalcs;
    private BigDecimal totalUnusualItemsExcludingGoodwill;
    private BigDecimal totalUnusualItems;
    private BigDecimal taxEffectOfUnusualItems;
    private BigDecimal rentExpenseSupplemental;
    private BigDecimal averageDilutionEarnings;
    private BigDecimal creditLossesProvision;
    private BigDecimal nonInterestExpense;
    private BigDecimal specialIncomeCharges;
    private BigDecimal interestIncomeAfterProvisionForLoanLoss;
    private BigDecimal totalMoneyMarketInvestments;
    private BigDecimal earningsFromEquityInterestNetOfTax;
    private BigDecimal incomeFromAssociatesAndOtherParticipatingInterests;
    private BigDecimal otherNonOperatingIncomeExpenses;

    // No getters and setters
}
