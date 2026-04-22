package com.weatherapp.weatherdashboard.repository;

import com.weatherapp.weatherdashboard.entity.WeatherHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//import java.util.List;

@Repository
public interface WeatherRepository extends JpaRepository<WeatherHistory, Long> {
    Optional<WeatherHistory> findTopByCityNameOrderBySearchTimeDesc(String cityName);
    List<WeatherHistory> findBySearchTimeAfter(LocalDateTime since);
    long countByCityName(String cityName);
    @Query("SELECT w.cityName, COUNT(w) as cnt FROM WeatherHistory w " + "GROUP BY w.cityName ORDER BY cnt DESC")
    List<Object[]> findMostSearchedCities();
}
