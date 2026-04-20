package com.weatherapp.weatherdashboard.repository;

import com.weatherapp.weatherdashboard.entity.WeatherHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//import java.util.List;

@Repository
public interface WeatherRepository extends JpaRepository<WeatherHistory, Long> {
    //List<WeatherHistory> findByCityNameOrderBySearchTimeDesc(String cityName);
}
