package com.weatherapp.weatherdashboard.exception;

import com.weatherapp.weatherdashboard.controller.WeatherController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@ControllerAdvice(assignableTypes = WeatherController.class)
public class WebExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(WebExceptionHandler.class);
    @ExceptionHandler(CityNotFoundException.class)
    public String handleCityNotFound(CityNotFoundException ex, Model model) {
        log.warn("City not found: {}", ex.getMessage());

        model.addAttribute("statusCode", 404);
        model.addAttribute("title", "City Not Found");
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("suggestion", "Check the spelling or try a nearby city.");
        return "error";
    }
    @ExceptionHandler(HttpClientErrorException.class)
    public String handleClientError(HttpClientErrorException ex, Model model) {
        log.warn("WeatherAPI client error: {} {}", ex.getStatusCode(), ex.getMessage());

        model.addAttribute("statusCode", ex.getStatusCode().value());
        model.addAttribute("title", "Invalid Request");
        model.addAttribute("error", "WeatherAPI could not understand the request.");
        model.addAttribute("suggestion", "Try a different city name or format.");
        return "error";
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public String handleApiDown(HttpServerErrorException ex, Model model) {
        log.error("WeatherAPI server error: {} {}", ex.getStatusCode(), ex.getMessage());

        model.addAttribute("statusCode", 503);
        model.addAttribute("title", "Service Unavailable");
        model.addAttribute("error", "Weather API is currently unavailable.");
        model.addAttribute("suggestion", "Please try again in a few minutes.");
        return "error";
    }

    @ExceptionHandler(ResourceAccessException.class)
    public String handleNetworkError(ResourceAccessException ex, Model model) {
        log.error("Network error reaching WeatherAPI: {}", ex.getMessage());

        model.addAttribute("statusCode", 503);
        model.addAttribute("title", "Connection Failed");
        model.addAttribute("error", "Could not reach the Weather API.");
        model.addAttribute("suggestion", "Check your internet connection and try again.");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        model.addAttribute("statusCode", 500);
        model.addAttribute("title", "Something Went Wrong");
        model.addAttribute("error", "An unexpected error occurred.");
        model.addAttribute("suggestion", "Please try again later.");
        return "error";
    }
}
