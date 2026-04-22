package com.weatherapp.weatherdashboard.controller;

import com.weatherapp.weatherdashboard.dto.AstronomyDTO;
import com.weatherapp.weatherdashboard.dto.ForecastDTO;
import com.weatherapp.weatherdashboard.entity.WeatherHistory;
import com.weatherapp.weatherdashboard.service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class WeatherController {
    private final WeatherService weatherService;
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/weather")
    public String getWeather(@RequestParam String city, Model model) {
        WeatherHistory weather = weatherService.getAndSaveWeather(city);
        model.addAttribute("weather", weather);
        model.addAttribute("city", city);
        return "weather";
    }

    @GetMapping("/forecast")
    public String getForecast(@RequestParam String city, Model model) {
        ForecastDTO forecast = weatherService.getForecast(city);
        model.addAttribute("forecast", forecast);
        model.addAttribute("city", city);
        model.addAttribute("days", forecast.getForecast().getForecastDay());
        return "forecast";
    }

    @GetMapping("/astronomy")
    public String getAstronomy(@RequestParam String city, Model model) {
        AstronomyDTO astronomy = weatherService.getAstronomy(city);
        model.addAttribute("astronomy", astronomy);
        model.addAttribute("city", city);

        // Unwrap the nested astro object for direct access in template
        model.addAttribute("astro", astronomy.getAstronomy().getAstro());
        return "astronomy";
    }

    @GetMapping("/history")
    public String viewHistory(Model model) {
        List<WeatherHistory> history = weatherService.getAllHistory();
        model.addAttribute("historyList", history);
        return "history";
    }
}
