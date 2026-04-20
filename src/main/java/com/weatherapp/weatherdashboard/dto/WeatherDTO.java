package com.weatherapp.weatherdashboard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class WeatherDTO {

    private Location location;
    private Current current;

    @Data
    public static class Location {
        private String name;
        private String region;
        private String country;
    }

    @Data
    public static class Current {
        @JsonProperty("temp_c")
        private Double tempC;

        private Condition condition;

        @JsonProperty("humidity")
        private Integer humidity;

        @JsonProperty("wind_kph")
        private Double windKph;
    }
    @Data
    public static class Condition {
        private String text;
        private String icon;
    }
}
