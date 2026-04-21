package com.weatherapp.weatherdashboard.dto.shared;

import lombok.Data;

@Data
public class ConditionDTO {
    private String text;
    private String icon;
    private int code;
}
