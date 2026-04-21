package com.weatherapp.weatherdashboard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.weatherapp.weatherdashboard.dto.shared.LocationDTO;
import lombok.Data;

@Data
public class AstronomyDTO {

    private LocationDTO location;
    private AstronomyWrapper astronomy;

    @Data
    public static class AstronomyWrapper {
        private AstroDTO astro;
    }

    @Data
    public static class AstroDTO {
        private String sunrise;     // "06:15 AM"
        private String sunset;      // "06:32 PM"
        private String moonrise;    // "11:45 PM"
        private String moonset;     // "11:02 AM"

        @JsonProperty("moon_phase")
        private String moonPhase;   // "Waning Gibbous"

        @JsonProperty("moon_illumination")
        private Integer moonIllumination; // 72 (percent)

        @JsonProperty("is_moon_up")
        private Integer isMoonUp;   // 0 or 1

        @JsonProperty("is_sun_up")
        private Integer isSunUp;    // 0 or 1
    }
}
