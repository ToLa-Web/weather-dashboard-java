package com.weatherapp.weatherdashboard.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_history")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"alert"})
@EqualsAndHashCode(of = "id")
public class WeatherHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city_name", nullable = false, length = 100)
    private String cityName;

    @Column(nullable = false)
    private Double temperature;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private Integer humidity;

    @Column(name = "wind_speed", nullable = false)
    private Double windSpeed;

    @Column(length = 500)
    private String alert;

    @Column(name = "search_time", nullable = false, updatable = false)
    private LocalDateTime searchTime;

    @PrePersist
    protected void onCreate() {
        searchTime = LocalDateTime.now();
    }
}
