package com.weatherapp.weatherdashboard.repository;

import com.weatherapp.weatherdashboard.entity.ForecastHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForecastRepository extends JpaRepository<ForecastHistory, Long> {
    List<ForecastHistory> findByCityNameOrderBySearchTimeDesc(String cityName);
}
