package com.example.weather.model;

import java.util.List;

public class WeatherData {
    private String city;
    private List<DayForecast> forecasts;
    
    public WeatherData(String city, List<DayForecast> forecasts) {
        this.city = city;
        this.forecasts = forecasts;
    }
    
    public String getCity() { return city; }
    public List<DayForecast> getForecasts() { return forecasts; }
    
    public static class DayForecast {
        private String date;
        private String temperature;
        private String description;
        private String source;
        
        public DayForecast(String date, String temperature, String description, String source) {
            this.date = date;
            this.temperature = temperature;
            this.description = description;
            this.source = source;
        }
        
        public String getDate() { return date; }
        public String getTemperature() { return temperature; }
        public String getDescription() { return description; }
        public String getSource() { return source; }
    }
}