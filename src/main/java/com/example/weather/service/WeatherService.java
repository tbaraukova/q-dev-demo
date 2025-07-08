package com.example.weather.service;

import com.example.weather.model.WeatherData;
import com.example.weather.model.WeatherData.DayForecast;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class WeatherService {
    private final RestTemplate restTemplate = new RestTemplate();
    
    public WeatherData getWeatherForecast(String city, int days) {
        List<DayForecast> forecasts = new ArrayList<>();
        String errorMessage1 = null;
        String errorMessage2 = null;
        
        // API 1: wttr.in (free with no auth required)
        try {
            String url1 = "https://wttr.in/" + city + "?format=j1";
            Map<String, Object> response1 = restTemplate.getForObject(url1, Map.class);
            if (response1 != null) {
                // Current weather
                List<Map<String, Object>> currentConditions = (List<Map<String, Object>>) response1.get("current_condition");
                if (currentConditions != null && !currentConditions.isEmpty()) {
                    Map<String, Object> currentWeather = currentConditions.get(0);
                    List<Map<String, String>> weatherDesc = (List<Map<String, String>>) currentWeather.get("weatherDesc");
                    String description = weatherDesc != null && !weatherDesc.isEmpty() ? weatherDesc.get(0).get("value") : "Unknown";
                    
                    forecasts.add(new DayForecast(
                        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        currentWeather.get("temp_C") + "°C",
                        description,
                        "wttr.in"
                    ));
                }
                
                // Forecast for next days
                List<Map<String, Object>> forecast = (List<Map<String, Object>>) response1.get("weather");
                if (forecast != null) {
                    int forecastDays = Math.min(days - 1, forecast.size());
                    for (int i = 0; i < forecastDays; i++) {
                        Map<String, Object> day = forecast.get(i);
                        String date = (String) day.get("date");
                        List<Map<String, Object>> hourly = (List<Map<String, Object>>) day.get("hourly");
                        
                        if (hourly != null && hourly.size() >= 4) {
                            Map<String, Object> noon = hourly.get(4); // Midday forecast (index 4 = noon)
                            List<Map<String, String>> weatherDesc = (List<Map<String, String>>) noon.get("weatherDesc");
                            String description = weatherDesc != null && !weatherDesc.isEmpty() ? weatherDesc.get(0).get("value") : "Unknown";
                            
                            forecasts.add(new DayForecast(
                                date,
                                day.get("avgtempC") + "°C",
                                description,
                                "wttr.in"
                            ));
                        }
                    }
                }
            }
        } catch (Exception e) {
            errorMessage1 = "wttr.in API Error: " + e.getMessage();
        }
        
        // API 2: GoWeather (free, no auth required)
        List<DayForecast> goWeatherForecasts = new ArrayList<>();
        try {
            String url2 = "https://goweather.herokuapp.com/weather/" + city;
            Map<String, Object> response2 = restTemplate.getForObject(url2, Map.class);
            if (response2 != null) {
                String temperature = (String) response2.get("temperature");
                String description = (String) response2.get("description");
                List<Map<String, String>> forecast = (List<Map<String, String>>) response2.get("forecast");
                
                // Add current day
                LocalDate today = LocalDate.now();
                goWeatherForecasts.add(new DayForecast(
                    today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    temperature,
                    description,
                    "GoWeather"
                ));
                
                // Add forecast days
                int forecastDays = Math.min(days - 1, forecast.size());
                for (int i = 0; i < forecastDays; i++) {
                    Map<String, String> day = forecast.get(i);
                    LocalDate forecastDate = today.plusDays(i + 1);
                    goWeatherForecasts.add(new DayForecast(
                        forecastDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        day.get("temperature"),
                        day.get("wind"),
                        "GoWeather"
                    ));
                }
                
                // Add GoWeather forecasts to the main list
                forecasts.addAll(goWeatherForecasts);
            }
        } catch (Exception e) {
            errorMessage2 = "GoWeather API Error: " + e.getMessage();
        }
        
        // Add error messages if both APIs failed
        if (forecasts.isEmpty() && errorMessage1 != null && errorMessage2 != null) {
            forecasts.add(new DayForecast(
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                "N/A",
                errorMessage1,
                "API Error"
            ));
            forecasts.add(new DayForecast(
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                "N/A",
                errorMessage2,
                "API Error"
            ));
        } else if (errorMessage1 != null) {
            // Add Open-Meteo error as a note
            forecasts.add(new DayForecast(
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                "N/A",
                errorMessage1,
                "API Error"
            ));
        } else if (errorMessage2 != null) {
            // Add GoWeather error as a note
            forecasts.add(new DayForecast(
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                "N/A",
                errorMessage2,
                "API Error"
            ));
        }
        
        return new WeatherData(city, forecasts);
    }
    

}