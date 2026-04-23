package com.weatherapp.weatherdashboard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IpLookupDTO {
    private String ip;
    private String type;
    private String city;
    private String region;
    private String country;
    private String lat;
    private String lon;

    @JsonProperty("tz_id")
    private String tzId;

    @JsonProperty("localtime")
    private String localtime;
}
