package com.weatherapp.weatherdashboard.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "weather_history")
@Data
public class WeatherHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cityName;
    private Double temperature;
    private String description;
    private Integer humidity;
    private Double windSpeed;

    private LocalDateTime searchTime;

    @PrePersist
    protected void onCreate() {
        searchTime = LocalDateTime.now();
    }
}
