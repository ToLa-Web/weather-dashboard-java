package com.weatherapp.weatherdashboard.controller;

import com.weatherapp.weatherdashboard.dto.*;
import com.weatherapp.weatherdashboard.entity.WeatherHistory;
import com.weatherapp.weatherdashboard.service.WeatherService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class WeatherRestController {
    private final WeatherService  weatherService;
    public WeatherRestController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather/{city}")
    public ResponseEntity<WeatherHistory> getWeatherByCity(@PathVariable String city) {
        return ResponseEntity.ok(weatherService.getAndSaveWeather(city));
    }

    @GetMapping("/forecast/{city}")
    public ResponseEntity<?> getForecast(@PathVariable String city) {
        return ResponseEntity.ok(weatherService.getForecast(city));
    }

    @GetMapping("/astronomy/{city}")
    public ResponseEntity<AstronomyDTO> getAstronomy(@PathVariable String city) {
        return ResponseEntity.ok(weatherService.getAstronomy(city));
    }

    @GetMapping("/timezone/{city}")
    public ResponseEntity<TimezoneDTO> getTimezone(@PathVariable String city) {
        return ResponseEntity.ok(weatherService.getTimezone(city));
    }

    @GetMapping("/sports/{city}")
    public ResponseEntity<SportsDTO> getSports(@PathVariable String city) {
        return ResponseEntity.ok(weatherService.getSports(city));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SearchResultDTO>> search(@RequestParam String q) {
        return ResponseEntity.ok(weatherService.searchCities(q));
    }

    @GetMapping("/detect-location")
    public ResponseEntity<IpLookupDTO> detectLocation() {
        return ResponseEntity.ok(weatherService.detectLocation());
    }

    @GetMapping("/history")
    public ResponseEntity<Page<WeatherHistory>> getHistory(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(weatherService.getAllHistory(page));
    }

    @GetMapping("/stats/top-cities")
    public ResponseEntity<List<Object[]>> getTopCities() {
        return ResponseEntity.ok(weatherService.getMostSearchedCities());
    }

    @GetMapping("/stats/total")
    public ResponseEntity<Long> getTotalSearches() {
        return ResponseEntity.ok(weatherService.getTotalSearches());
    }

}
