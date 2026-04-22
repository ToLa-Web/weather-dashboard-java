package com.weatherapp.weatherdashboard.exception;

import org.springframework.ui.Model;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CityNotFoundException.class)
    public String handleCityNotFound(CityNotFoundException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("statusCode", 404);
        return "error";
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public String handleHttpMediaTypeException(HttpMediaTypeException ex, Model model) {
        model.addAttribute("error", "Weather API is unavailable. Try again later.");
        model.addAttribute("statusCode", 503);
        return "error";
    }
}
