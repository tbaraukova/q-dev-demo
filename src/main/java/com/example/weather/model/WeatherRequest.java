package com.example.weather.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class WeatherRequest {
    @NotBlank(message = "City is required")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "City must contain only letters and spaces")
    private String city;
    
    @Pattern(regexp = "^(1|3|7)$", message = "Days must be 1, 3, or 7")
    private String days = "1";
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getDays() { return days; }
    public void setDays(String days) { this.days = days; }
}