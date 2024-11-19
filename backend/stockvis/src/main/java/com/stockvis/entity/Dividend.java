package com.stockvis.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "dividend")
public class Dividend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dividendId;

    @ManyToOne
    @JoinColumn(name = "ticker", nullable = false)
    private Stock stock;

    private LocalDate date;
    private Double dividendPerShare;

    // Getters and Setters
}
