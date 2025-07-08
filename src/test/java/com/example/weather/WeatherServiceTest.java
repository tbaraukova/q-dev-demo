package com.example.weather;

import com.example.weather.model.WeatherData;
import com.example.weather.service.WeatherService;
import org.testng.annotations.Test;
import org.testng.Assert;

public class WeatherServiceTest {
    
    @Test
    public void testGetWeatherForecast() {
        WeatherService service = new WeatherService();
        WeatherData result = service.getWeatherForecast("London", 3);
        
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getCity(), "London");
        Assert.assertTrue(result.getForecasts().size() > 0, "Should have at least one forecast");
        Assert.assertNotNull(result.getForecasts().get(0).getDate());
        Assert.assertNotNull(result.getForecasts().get(0).getTemperature());
    }
}