package com.weatherapp.weatherdashboard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.weatherapp.weatherdashboard.dto.shared.ConditionDTO;
import com.weatherapp.weatherdashboard.dto.shared.LocationDTO;
import lombok.Data;

import java.util.List;

@Data
public class ForecastDTO {

    private LocationDTO location;
    private ForecastWrapper forecast;

    @Data
    public static class ForecastWrapper {
        @JsonProperty("forecastday")
        private List<ForecastDayDTO> forecastDay;
    }
    @Data
    public static class ForecastDayDTO {
        private String date;
        private DayDTO day;
        private AstroDTO astro;
        private List<HourDTO> hour;
    }

    @Data
    public static class DayDTO {
        @JsonProperty("maxtemp_c")
        private Double maxTempC;

        @JsonProperty("mintemp_c")
        private Double minTempC;

        @JsonProperty("avgtemp_c")
        private Double avgTempC;

        @JsonProperty("maxwind_kph")
        private Double maxWindKph;

        @JsonProperty("totalprecip_mm")
        private Double totalPrecipMm;

        @JsonProperty("avghumidity")
        private Integer avgHumidity;

        @JsonProperty("daily_chance_of_rain")
        private Integer chanceOfRain;

        @JsonProperty("daily_chance_of_snow")
        private Integer chanceOfSnow;

        @JsonProperty("uv")
        private Double uvIndex;

        private ConditionDTO condition;
    }

    @Data
    public static class AstroDTO {
        private String sunrise;
        private String sunset;
        private String moonrise;
        private String moonset;

        @JsonProperty("moon_phase")
        private String moonPhase;

        @JsonProperty("moon_illumination")
        private Integer moonIllumination;
    }

    @Data
    public static class HourDTO {
        private String time;              // "2025-04-21 00:00"

        @JsonProperty("temp_c")
        private Double tempC;

        @JsonProperty("feelslike_c")
        private Double feelsLikeC;

        @JsonProperty("wind_kph")
        private Double windKph;

        @JsonProperty("wind_dir")
        private String windDir;

        private Integer humidity;

        @JsonProperty("chance_of_rain")
        private Integer chanceOfRain;

        @JsonProperty("chance_of_snow")
        private Integer chanceOfSnow;

        @JsonProperty("precip_mm")
        private Double precipMm;

        @JsonProperty("uv")
        private Double uvIndex;

        @JsonProperty("is_day")
        private Integer isDay;           // 1 = day, 0 = night

        private ConditionDTO condition;
    }
}
