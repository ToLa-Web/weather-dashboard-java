package com.weatherapp.weatherdashboard.exception;

public class CityNotFoundException extends RuntimeException {
    public CityNotFoundException(String cityName) {
        super("City " + cityName + " not found");
    }
}
