package com.weatherapp.weatherdashboard.service;

import com.weatherapp.weatherdashboard.dto.AstronomyDTO;
import com.weatherapp.weatherdashboard.dto.ForecastDTO;
import com.weatherapp.weatherdashboard.dto.WeatherDTO;
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
import java.util.List;

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
