package com.weatherapp.weatherdashboard.exception;

import com.weatherapp.weatherdashboard.controller.WeatherRestController;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice(assignableTypes = WeatherRestController.class)
public class RestExceptionHandler {
    public record ErrorResponse(
            int status,
            String title,
            String message,
            String path,
            LocalDateTime timestamp  // ← real world always includes timestamp
    ) {}

    @ExceptionHandler(CityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCityNotFound(
            CityNotFoundException ex,
            HttpServletRequest request) {
        log.warn("City not found: {}", ex.getMessage());
        return ResponseEntity.status(404).body(new ErrorResponse(
                404,
                "City Not Found",
                ex.getMessage(),
                request.getRequestURI(),   // ← tells client which URL failed
                LocalDateTime.now()
        ));
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleClientError(
            HttpClientErrorException ex,
            HttpServletRequest request) {
        log.warn("WeatherAPI client error: {}", ex.getStatusCode());
        return ResponseEntity.status(ex.getStatusCode()).body(new ErrorResponse(
                ex.getStatusCode().value(),
                "Invalid Request",
                "WeatherAPI could not understand the request.",
                request.getRequestURI(),
                LocalDateTime.now()
        ));
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleApiDown(
            HttpServletRequest request) {
        log.error("WeatherAPI server error");
        return ResponseEntity.status(503).body(new ErrorResponse(
                503,
                "Service Unavailable",
                "Weather API is currently unavailable.",
                request.getRequestURI(),
                LocalDateTime.now()
        ));
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleNetworkError(
            ResourceAccessException ex,
            HttpServletRequest request) {
        log.error("Network error: {}", ex.getMessage());
        return ResponseEntity.status(503).body(new ErrorResponse(
                503,
                "Connection Failed",
                "Could not reach the Weather API.",
                request.getRequestURI(),
                LocalDateTime.now()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(500).body(new ErrorResponse(
                500,
                "Internal Server Error",
                "An unexpected error occurred.",
                request.getRequestURI(),
                LocalDateTime.now()
        ));
    }
}
