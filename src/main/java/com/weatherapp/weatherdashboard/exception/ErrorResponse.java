package com.weatherapp.weatherdashboard.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String title,
        String message,
        String path,
        LocalDateTime timestamp
) {}