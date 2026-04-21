package com.weatherapp.weatherdashboard.dto.shared;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LocationDTO {
    private String name;
    private String region;
    private String country;
    private Double latitude;
    private Double longitude;

    @JsonProperty("tz_id")
    private String tzId;
    @JsonProperty("localtime")
    private String localTime;
}
