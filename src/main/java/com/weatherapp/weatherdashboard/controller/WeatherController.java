package com.weatherapp.weatherdashboard.controller;

import com.weatherapp.weatherdashboard.dto.*;
import com.weatherapp.weatherdashboard.entity.WeatherHistory;
import com.weatherapp.weatherdashboard.service.WeatherService;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
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

    @GetMapping("/detect-location")
    public String detectLocation() {
        log.info("📍 Auto-detecting user location");

        IpLookupDTO location = weatherService.detectLocation();
        log.info("✅ Location detected: {} ({}, {})",
                 location.getCity(), location.getRegionName(), location.getCountryName());

        return "redirect:/weather?city=" +
                URLEncoder.encode(location.getCity(), StandardCharsets.UTF_8);
    }

    @GetMapping("/weather")
    public String getWeather(@RequestParam @NotBlank(message = "City cannot be empty") String city, Model model) {
        log.info("🌤️ User requested weather for: {}", city);

        WeatherHistory weather = weatherService.getAndSaveWeather(city);
        model.addAttribute("weather", weather);
        model.addAttribute("city", city);

        log.debug("📊 Weather rendered: {}°C, {}% humidity, {}",
                  weather.getTemperature(), weather.getHumidity(), weather.getDescription());
        return "weather";
    }

    @GetMapping("/forecast")
    public String getForecast(@RequestParam String city, Model model) {
        log.info("📅 User requested forecast for: {}", city);

        ForecastDTO forecast = weatherService.getForecast(city);
        model.addAttribute("forecast", forecast);
        model.addAttribute("city", city);
        model.addAttribute("days", forecast.getForecast().getForecastDay());

        log.debug("📊 Forecast rendered with {} days for: {}", forecast.getForecast().getForecastDay().size(), city);
        return "forecast";
    }

    @GetMapping("/astronomy")
    public String getAstronomy(@RequestParam String city, Model model) {
        log.info("🌙 User requested astronomy data for: {}", city);

        AstronomyDTO astronomy = weatherService.getAstronomy(city);
        model.addAttribute("astronomy", astronomy);
        model.addAttribute("city", city);
        model.addAttribute("astro", astronomy.getAstronomy().getAstro());

        log.debug("📊 Astronomy rendered: sunrise={}, sunset={}",
                  astronomy.getAstronomy().getAstro().getSunrise(),
                  astronomy.getAstronomy().getAstro().getSunset());
        return "astronomy";
    }

    @GetMapping("/timezone")
    public String getTimezone(@RequestParam String city, Model model) {
        log.info("🕐 User requested timezone info for: {}", city);

        TimezoneDTO timezone = weatherService.getTimezone(city);
        model.addAttribute("tz", timezone);
        model.addAttribute("city", city);

        log.debug("📊 Timezone rendered: {}", timezone.getLocation().getTzId());
        return "timezone";
    }

    @GetMapping("/sports")
    public String getSports(@RequestParam String city, Model model) {
        log.info("⚽ User requested sports events for: {}", city);

        SportsDTO sports = weatherService.getSports(city);
        model.addAttribute("sports", sports);
        model.addAttribute("city", city);

        log.debug("📊 Sports events rendered for: {}", city);
        return "sports";
    }
    @GetMapping("/history")
    public String viewHistory(@RequestParam(defaultValue = "0") int page, Model model) {
        log.info("📜 User viewing search history - page: {}", page);

        Page<WeatherHistory> history = weatherService.getAllHistory(page);
        model.addAttribute("historyList", history.getContent());
        model.addAttribute("totalPage", history.getTotalPages());
        model.addAttribute("currentPage", page);

        log.debug("📊 History page {} displayed with {} records (Total pages: {})",
                  page, history.getContent().size(), history.getTotalPages());
        return "history";
    }

    @GetMapping("/stats")
    public String getStats(Model model) {
        log.info("📊 User viewing statistics");

        model.addAttribute("topCities",     weatherService.getMostSearchedCities());
        model.addAttribute("recentSearches",weatherService.getRecentSearches());
        model.addAttribute("totalSearches", weatherService.getTotalSearches());

        log.debug("📊 Stats loaded: Total searches: {}", weatherService.getTotalSearches());
        return "stats";
    }
}
