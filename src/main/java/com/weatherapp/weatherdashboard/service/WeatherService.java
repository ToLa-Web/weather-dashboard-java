package com.weatherapp.weatherdashboard.service;

import com.weatherapp.weatherdashboard.dto.*;
import com.weatherapp.weatherdashboard.entity.WeatherHistory;
import com.weatherapp.weatherdashboard.exception.CityNotFoundException;
import com.weatherapp.weatherdashboard.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WeatherService {
    private final WeatherRepository weatherRepo;
    private final RestTemplate restTemplate;

    @Value("${weather.api.key}")
    private String apiKey;
    @Value("${weather.api.base-url}")
    private String baseUrl;

    public WeatherService(WeatherRepository weatherRepo, RestTemplate restTemplate) {
        this.weatherRepo = weatherRepo;
        this.restTemplate = restTemplate;
    }

    @Cacheable(value = "weather", key = "#city", unless = "#result == null")
    public WeatherHistory getAndSaveWeather(String city) {
        //Check DB first if searched within last 10 minutes, reuse it
        Optional<WeatherHistory> cached = weatherRepo.findTopByCityNameOrderBySearchTimeDesc(city);
        if (cached.isPresent()) {
            LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);
            if (cached.get().getSearchTime().isAfter(tenMinutesAgo)) {
                return cached.get(); // ← return DB result, skip API call
            }
        }
        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/current.json")
                .queryParam("key", apiKey)
                .queryParam("q", city)
                .build()
                .toUriString();
        try {
            WeatherDTO response = restTemplate.getForObject(url, WeatherDTO.class);
            if (response == null) throw new CityNotFoundException(city);
            WeatherHistory entity = convertToEntity(response);
            return weatherRepo.save(entity);
        }catch (HttpClientErrorException.BadRequest |  HttpClientErrorException.NotFound e) {
            throw new CityNotFoundException(city);
        }
    }

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

    public List<WeatherHistory> getAllHistory() {
        return weatherRepo.findAll();
    }

    public ForecastDTO getForecast(String city) {
        String url = UriComponentsBuilder
                .fromUriString(baseUrl + "/forecast.json")
                .queryParam("key", apiKey)
                .queryParam("q", city)
                .queryParam("days", 3)
                .queryParam("alerts", "yes")
                .toUriString();
        ForecastDTO result = restTemplate.getForObject(url, ForecastDTO.class);
        if (result == null) throw new CityNotFoundException(city);
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
