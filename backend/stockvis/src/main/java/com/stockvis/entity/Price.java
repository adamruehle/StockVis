package com.stockvis.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "price")
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long priceId;

    @ManyToOne
    @JoinColumn(name = "ticker", nullable = false)
    private Stock stock;

    private LocalDate date;
    private Double openPrice;
    private Double closePrice;
    private Double lowPrice;
    private Double highPrice;
    private Long volume;

    // Getters and Setters
}
