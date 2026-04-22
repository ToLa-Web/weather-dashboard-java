package com.weatherapp.weatherdashboard.dto;

import lombok.Data;

import java.util.List;

@Data
public class SportsDTO {

    private List<SportEventDTO> football;
    private List<SportEventDTO> cricket;
    private List<SportEventDTO> golf;

    @Data
    public static class SportEventDTO {
        private String stadium;
        private String country;
        private String region;
        private String tournament;
        private String start;      // "2025-04-22 15:00"
        private String match;      // "Team A vs Team B"
    }

}
