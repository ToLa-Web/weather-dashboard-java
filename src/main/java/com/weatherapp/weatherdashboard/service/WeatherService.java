package com.weatherapp.weatherdashboard.service;

import com.weatherapp.weatherdashboard.dto.*;
import com.weatherapp.weatherdashboard.entity.ForecastHistory;
import com.weatherapp.weatherdashboard.entity.WeatherHistory;
import com.weatherapp.weatherdashboard.exception.CityNotFoundException;
import com.weatherapp.weatherdashboard.repository.ForecastRepository;
import com.weatherapp.weatherdashboard.repository.WeatherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class WeatherService {
    private final WeatherRepository weatherRepo;
    private final ForecastRepository forecastRepo;
    private final RestTemplate restTemplate;

    @Value("${weather.api.key}")
    private String apiKey;
    @Value("${weather.api.base-url}")
    private String baseUrl;

    public WeatherService(WeatherRepository weatherRepo, ForecastRepository forecastRepo, RestTemplate restTemplate) {
        this.weatherRepo = weatherRepo;
        this.forecastRepo = forecastRepo;
        this.restTemplate = restTemplate;
    }

    @Cacheable(value = "weather", key = "#city", unless = "#result == null")
    public WeatherHistory getAndSaveWeather(String city) {
        log.info("🔍 Searching weather for: {}", city);

        //Check DB first if searched within last 10 minutes, reuse it
        Optional<WeatherHistory> cached = weatherRepo.findTopByCityNameOrderBySearchTimeDesc(city);
        if (cached.isPresent()) {
            LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);
            if (cached.get().getSearchTime().isAfter(tenMinutesAgo)) {
                log.debug("✅ Cache HIT for: {} - Using DB record", city);
                return cached.get(); // ← return DB result, skip API call
            }
        }

        log.debug("❌ Cache MISS for: {} - Calling external API", city);
        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/current.json")
                .queryParam("key", apiKey)
                .queryParam("q", city)
                .build()
                .toUriString();
        try {
            log.debug("📡 Making API call to weatherapi.com for: {}", city);
            WeatherDTO response = restTemplate.getForObject(url, WeatherDTO.class);
            if (response == null) throw new CityNotFoundException(city);

            WeatherHistory entity = convertToEntity(response);
            log.info("💾 Saving weather: City={}, Temp={}°C, Humidity={}%, Condition={}",
                     entity.getCityName(), entity.getTemperature(), entity.getHumidity(), entity.getDescription());
            return weatherRepo.save(entity);
        }catch (HttpClientErrorException.BadRequest |  HttpClientErrorException.NotFound e) {
            log.error("❌ City not found: {} - Status: {}", city, e.getStatusCode());
            throw new CityNotFoundException(city);
        }
    }

    @Cacheable(value = "cities", key = "#query", unless = "#result == null || #result.isEmpty()")
    public List<SearchResultDTO> searchCities(String query) {
        if (query == null || query.trim().length() < 2) {
            return List.of();
        }

        String url = UriComponentsBuilder
                .fromUriString(baseUrl + "/search.json")
                .queryParam("key", apiKey)
                .queryParam("q", query)
                .toUriString();

        SearchResultDTO[] results = restTemplate
                .getForObject(url, SearchResultDTO[].class);

        return results != null ? List.of(results) : List.of();
    }

    public Page<WeatherHistory> getAllHistory(int page) {
        return weatherRepo.findAll(PageRequest.of(page, 20));
    }

    public ForecastDTO getForecast(String city) {
        log.info("📅 Fetching 3-day forecast for: {}", city);

        String url = UriComponentsBuilder
                .fromUriString(baseUrl + "/forecast.json")
                .queryParam("key", apiKey)
                .queryParam("q", city)
                .queryParam("days", 3)
                .queryParam("alerts", "yes")
                .toUriString();

        log.debug("📡 Calling forecast API for: {}", city);
        ForecastDTO result = restTemplate.getForObject(url, ForecastDTO.class);
        if (result != null) {
            for (ForecastDTO.ForecastDayDTO day : result.getForecast().getForecastDay()) {
                ForecastHistory entity = new ForecastHistory();
                entity.setCityName(city);
                entity.setForecastDay(day.getDate());
                entity.setMaxTempC(day.getDay().getMaxTempC());
                entity.setMinTempC(day.getDay().getMinTempC());
                entity.setCondition(day.getDay().getCondition().getText());

                log.debug("💾 Saving forecast: City={}, Date={}, Max={}°C, Min={}°C, Condition={}",
                         city, day.getDate(), entity.getMaxTempC(), entity.getMinTempC(), entity.getCondition());
                forecastRepo.save(entity);
            }
            log.info("✅ Forecast saved successfully for: {} ({} days)", city, result.getForecast().getForecastDay().size());
        }
        return result;
    }

    public AstronomyDTO getAstronomy(String city) {
        String url = UriComponentsBuilder
                .fromUriString(baseUrl + "/astronomy.json")
                .queryParam("key", apiKey)
                .queryParam("q", city)
                .queryParam("dt", LocalDate.now())
                .toUriString();
        AstronomyDTO result = restTemplate.getForObject(url, AstronomyDTO.class);
        if (result == null) throw new CityNotFoundException(city);
        return result;
    }

    // WeatherService.java
    public TimezoneDTO getTimezone(String city) {
        String url = UriComponentsBuilder
                .fromUriString(baseUrl + "/timezone.json")
                .queryParam("key", apiKey)
                .queryParam("q", city)
                .toUriString();

        TimezoneDTO result = restTemplate.getForObject(url, TimezoneDTO.class);
        if (result == null) throw new CityNotFoundException(city);
        return result;
    }

    public SportsDTO getSports(String city) {
        String url = UriComponentsBuilder
                .fromUriString(baseUrl + "/sports.json")
                .queryParam("key", apiKey)
                .queryParam("q", city)
                .toUriString();

        SportsDTO result = restTemplate.getForObject(url, SportsDTO.class);
        if (result == null) throw new CityNotFoundException(city);
        return result;
    }

    public List<Object[]> getMostSearchedCities() {
        return weatherRepo.findMostSearchedCities();
    }
    public List<WeatherHistory> getRecentSearches() {
        return weatherRepo.findBySearchTimeAfter(
                LocalDateTime.now().minusHours(24));
    }
    public long getCitySearchCount(String cityName) {
        return weatherRepo.countByCityName(cityName);
    }
    public long getTotalSearches() {
        return weatherRepo.count();
    }

    public IpLookupDTO detectLocation() {
        String url = UriComponentsBuilder
                .fromUriString(baseUrl + "/ip.json")
                .queryParam("key", apiKey)
                .queryParam("q", "auto:ip")
                .toUriString();

        IpLookupDTO result = restTemplate.getForObject(url, IpLookupDTO.class);
        if (result == null) throw new RuntimeException("Could not detect location");
        return result;
    }

    public String checkAlerts(WeatherHistory weather) {
        if (weather.getWindSpeed() > 50) {
            return "High Wind Alert! Stay indoors.";
        } else if (weather.getDescription().toLowerCase().contains("storm")) {
            return "Storm Warning! Plan accordingly.";
        }
        return null;
    }

    private WeatherHistory convertToEntity(WeatherDTO dto) {
        WeatherHistory entity = new WeatherHistory();
        entity.setCityName(dto.getLocation().getName());
        entity.setTemperature(dto.getCurrent().getTempC());
        entity.setDescription(dto.getCurrent().getCondition().getText());
        entity.setHumidity(dto.getCurrent().getHumidity());
        entity.setWindSpeed(dto.getCurrent().getWindKph());
        entity.setAlert(checkAlerts(entity));
        return entity;
    }
}
