package com.weatherapp.weatherdashboard.dto;

import com.weatherapp.weatherdashboard.dto.shared.LocationDTO;
import lombok.Data;

@Data
public class TimezoneDTO {
    private LocationDTO location;

    public String getLocalTime() {
        return location != null ? location.getLocalTime() : "--";
    }

    public String getTimezone() {
        return location != null ? location.getTzId() : "--";
    }

    public String getCityName() {
        return location != null ? location.getName() : "--";
    }
}
