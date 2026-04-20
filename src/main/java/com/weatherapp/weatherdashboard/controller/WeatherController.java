package com.weatherapp.weatherdashboard.controller;

import com.weatherapp.weatherdashboard.entity.WeatherHistory;
import com.weatherapp.weatherdashboard.service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/search")
    public String searchWeather(@RequestParam String city, Model model) {
        try{
            WeatherHistory weatherHis = weatherService.getAndSaveWeather(city);
            //String alert = weatherService.checkAlerts(weatherHis);
            model.addAttribute("weather", weatherHis);
        }catch (Exception e){
            model.addAttribute("error", e.getMessage());
        }
        return "index";
    }

    @GetMapping("/history")
    public String viewHistory(Model model) {
        List<WeatherHistory> history = weatherService.getAllHistory();
        model.addAttribute("historyList", history);
        return "history";
    }
}
