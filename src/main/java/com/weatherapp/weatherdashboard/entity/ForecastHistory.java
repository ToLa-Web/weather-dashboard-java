package com.weatherapp.weatherdashboard.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "forecast_history")
@Data
@NoArgsConstructor
public class ForecastHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cityName;

    @Column(nullable = false)
    private String forecastDay;

    private Double maxTempC;
    private Double minTempC;
    private String condition;

    @Column(name = "search_time", nullable = false)
    private LocalDateTime searchTime;

    @PrePersist
    protected void onCreate() {
        searchTime = LocalDateTime.now();
    }
}
