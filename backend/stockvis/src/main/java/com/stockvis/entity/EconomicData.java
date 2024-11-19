package com.stockvis.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "economicData")
public class EconomicData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long econId;

    private LocalDate date;
    private Double interestRate;
    private Double inflationRate;
    private Double unemploymentRate;
    private Double gdp;

    // Getters and Setters
}
