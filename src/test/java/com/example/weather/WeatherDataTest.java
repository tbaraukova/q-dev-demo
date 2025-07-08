package com.example.weather;

import com.example.weather.model.WeatherData;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class WeatherDataTest {

    @Test
    public void testWeatherDataConstructorAndGetters() {
        // Create test data
        List<WeatherData.DayForecast> forecasts = new ArrayList<>();
        forecasts.add(new WeatherData.DayForecast("2023-01-01", "20°C", "Sunny", "Test API"));
        forecasts.add(new WeatherData.DayForecast("2023-01-02", "22°C", "Partly Cloudy", "Test API"));
        
        // Create WeatherData object
        WeatherData weatherData = new WeatherData("London", forecasts);
        
        // Test getters
        Assert.assertEquals(weatherData.getCity(), "London");
        Assert.assertEquals(weatherData.getForecasts(), forecasts);
        Assert.assertEquals(weatherData.getForecasts().size(), 2);
    }
    
    @Test
    public void testDayForecastConstructorAndGetters() {
        // Create DayForecast object
        WeatherData.DayForecast forecast = new WeatherData.DayForecast(
            "2023-01-01", "20°C", "Sunny", "Test API");
        
        // Test getters
        Assert.assertEquals(forecast.getDate(), "2023-01-01");
        Assert.assertEquals(forecast.getTemperature(), "20°C");
        Assert.assertEquals(forecast.getDescription(), "Sunny");
        Assert.assertEquals(forecast.getSource(), "Test API");
    }
}