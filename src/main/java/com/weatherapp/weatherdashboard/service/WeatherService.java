package com.weatherapp.weatherdashboard.service;

import com.weatherapp.weatherdashboard.dto.WeatherDTO;
import com.weatherapp.weatherdashboard.entity.WeatherHistory;
import com.weatherapp.weatherdashboard.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    public WeatherHistory getAndSaveWeather(String city) {
        String url = String.format("%s/current.json?key=%s&q=%s", baseUrl, apiKey, city);

        WeatherDTO response = restTemplate.getForObject(url, WeatherDTO.class);

        if (response != null) {
            WeatherHistory entity = convertToEntity(response);

            return weatherRepo.save(entity);
        }
        throw new RuntimeException("City not found: " + city);
    }

    public List<WeatherHistory> getAllHistory() {
        return weatherRepo.findAll();
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
        return entity;
    }
}
