package com.stockvis.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "balance_sheet")
@IdClass(BalanceSheetId.class) // Composite primary key
public class BalanceSheet implements Serializable {

    @Id
    @Column(name = "company_id", length = 10)
    private String companyId;

    @Id
    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(name = "total_assets", precision = 15, scale = 2)
    private BigDecimal totalAssets;

    @Column(name = "total_liabilities_net_minority_interest", precision = 15, scale = 2)
    private BigDecimal totalLiabilitiesNetMinorityInterest;

    @Column(name = "total_equity_gross_minority_interest", precision = 15, scale = 2)
    private BigDecimal totalEquityGrossMinorityInterest;

    @Column(name = "total_capitalization", precision = 15, scale = 2)
    private BigDecimal totalCapitalization;

    @Column(name = "common_stock_equity", precision = 15, scale = 2)
    private BigDecimal commonStockEquity;

    @Column(name = "net_tangible_assets", precision = 15, scale = 2)
    private BigDecimal netTangibleAssets;

    @Column(name = "working_capital", precision = 15, scale = 2)
    private BigDecimal workingCapital;

    @Column(name = "invested_capital", precision = 15, scale = 2)
    private BigDecimal investedCapital;

    @Column(name = "tangible_book_value", precision = 15, scale = 2)
    private BigDecimal tangibleBookValue;

    @Column(name = "total_debt", precision = 15, scale = 2)
    private BigDecimal totalDebt;

    @Column(name = "net_debt", precision = 15, scale = 2)
    private BigDecimal netDebt;

    @Column(name = "share_issued", precision = 15, scale = 2)
    private BigDecimal shareIssued;

    @Column(name = "ordinary_shares_number", precision = 15, scale = 2)
    private BigDecimal ordinarySharesNumber;

    @Column(name = "capital_lease_obligations", precision = 15, scale = 2)
    private BigDecimal capitalLeaseObligations;

    @Column(name = "preferred_stock_equity", precision = 15, scale = 2)
    private BigDecimal preferredStockEquity;

    @Column(name = "preferred_shares_number", precision = 15, scale = 2)
    private BigDecimal preferredSharesNumber;

    @Column(name = "treasury_shares_number", precision = 15, scale = 2)
    private BigDecimal treasurySharesNumber;

}
