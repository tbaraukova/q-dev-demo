package com.example.weather;

import com.example.weather.model.WeatherData;
import com.example.weather.service.WeatherService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Assert;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class WeatherServiceTest {
    
    @Mock
    private RestTemplate restTemplate;
    
    @InjectMocks
    private WeatherService weatherService;
    
    @BeforeMethod
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Use reflection to set the mocked RestTemplate in the service
        try {
            java.lang.reflect.Field field = WeatherService.class.getDeclaredField("restTemplate");
            field.setAccessible(true);
            field.set(weatherService, restTemplate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testGetWeatherForecastOriginal() {
        WeatherService service = new WeatherService();
        WeatherData result = service.getWeatherForecast("London", 3);
        
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getCity(), "London");
        Assert.assertTrue(result.getForecasts().size() > 0, "Should have at least one forecast");
        Assert.assertTrue(result.getForecasts().size() <= 3, "Should have at most 3 forecasts");
        Assert.assertNotNull(result.getForecasts().get(0).getDate());
        Assert.assertNotNull(result.getForecasts().get(0).getTemperature());
    }
    
    @Test
    public void testGetWeatherForecastWithMockedWttrApi() {
        // Mock wttr.in API response
        Map<String, Object> wttrResponse = new HashMap<>();
        
        // Mock current condition
        Map<String, Object> currentWeather = new HashMap<>();
        currentWeather.put("temp_C", "20");
        
        List<Map<String, String>> weatherDesc = new ArrayList<>();
        Map<String, String> desc = new HashMap<>();
        desc.put("value", "Sunny");
        weatherDesc.add(desc);
        currentWeather.put("weatherDesc", weatherDesc);
        
        List<Map<String, Object>> currentConditions = new ArrayList<>();
        currentConditions.add(currentWeather);
        wttrResponse.put("current_condition", currentConditions);
        
        // Mock forecast
        List<Map<String, Object>> forecast = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Map<String, Object> day = new HashMap<>();
            day.put("date", LocalDate.now().plusDays(i+1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            day.put("avgtempC", "22");
            
            List<Map<String, Object>> hourly = new ArrayList<>();
            for (int h = 0; h < 8; h++) {
                Map<String, Object> hour = new HashMap<>();
                
                List<Map<String, String>> hourWeatherDesc = new ArrayList<>();
                Map<String, String> hourDesc = new HashMap<>();
                hourDesc.put("value", "Partly Cloudy");
                hourWeatherDesc.add(hourDesc);
                hour.put("weatherDesc", hourWeatherDesc);
                
                hourly.add(hour);
            }
            day.put("hourly", hourly);
            forecast.add(day);
        }
        wttrResponse.put("weather", forecast);
        
        // Mock GoWeather API to fail
        when(restTemplate.getForObject(eq("https://wttr.in/London?format=j1"), eq(Map.class)))
            .thenReturn(wttrResponse);
        when(restTemplate.getForObject(eq("https://goweather.herokuapp.com/weather/London"), eq(Map.class)))
            .thenThrow(new RuntimeException("API unavailable"));
        
        WeatherData result = weatherService.getWeatherForecast("London", 3);
        
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getCity(), "London");
        Assert.assertTrue(result.getForecasts().size() >= 3, "Should have at least 3 forecasts");
        Assert.assertTrue(result.getForecasts().size() <= 4, "Should have at most 4 forecasts (3 forecasts + 1 error)");
        
        // Check first forecast (current day)
        Assert.assertEquals(result.getForecasts().get(0).getSource(), "wttr.in");
        Assert.assertEquals(result.getForecasts().get(0).getTemperature(), "20°C");
        Assert.assertEquals(result.getForecasts().get(0).getDescription(), "Sunny");
        
        // Check if there's an error message for GoWeather
        boolean hasErrorMessage = false;
        for (WeatherData.DayForecast forecast1 : result.getForecasts()) {
            if (forecast1.getSource().equals("API Error") && 
                forecast1.getDescription().contains("GoWeather API Error")) {
                hasErrorMessage = true;
                break;
            }
        }
        Assert.assertTrue(hasErrorMessage, "Should contain GoWeather API error message");
    }
    
    @Test
    public void testGetWeatherForecastWithMockedGoWeatherApi() {
        // Mock wttr.in API to fail
        when(restTemplate.getForObject(eq("https://wttr.in/Paris?format=j1"), eq(Map.class)))
            .thenThrow(new RuntimeException("API unavailable"));
        
        // Mock GoWeather API response
        Map<String, Object> goWeatherResponse = new HashMap<>();
        goWeatherResponse.put("temperature", "25°C");
        goWeatherResponse.put("description", "Sunny");
        
        List<Map<String, String>> forecast = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Map<String, String> day = new HashMap<>();
            day.put("temperature", (25 - i) + "°C");
            day.put("wind", (10 + i) + " km/h");
            forecast.add(day);
        }
        goWeatherResponse.put("forecast", forecast);
        
        when(restTemplate.getForObject(eq("https://goweather.herokuapp.com/weather/Paris"), eq(Map.class)))
            .thenReturn(goWeatherResponse);
        
        WeatherData result = weatherService.getWeatherForecast("Paris", 3);
        
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getCity(), "Paris");
        Assert.assertTrue(result.getForecasts().size() >= 3, "Should have at least 3 forecasts");
        Assert.assertTrue(result.getForecasts().size() <= 4, "Should have at most 4 forecasts (3 forecasts + 1 error)");
        
        // Check first forecast (current day)
        Assert.assertEquals(result.getForecasts().get(0).getSource(), "GoWeather");
        Assert.assertEquals(result.getForecasts().get(0).getTemperature(), "25°C");
        Assert.assertEquals(result.getForecasts().get(0).getDescription(), "Sunny");
        
        // Check if there's an error message for wttr.in
        boolean hasErrorMessage = false;
        for (WeatherData.DayForecast forecast1 : result.getForecasts()) {
            if (forecast1.getSource().equals("API Error") && 
                forecast1.getDescription().contains("wttr.in API Error")) {
                hasErrorMessage = true;
                break;
            }
        }
        Assert.assertTrue(hasErrorMessage, "Should contain wttr.in API error message");
    }
    
    @Test
    public void testGetWeatherForecastWithBothApisFailing() {
        // Mock both APIs to fail
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
            .thenThrow(new RuntimeException("API unavailable"));
        
        WeatherData result = weatherService.getWeatherForecast("Berlin", 3);
        
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getCity(), "Berlin");
        Assert.assertEquals(result.getForecasts().size(), 2, "Should have 2 error messages");
        
        // Check that both forecasts are error messages
        Assert.assertEquals(result.getForecasts().get(0).getSource(), "API Error");
        Assert.assertTrue(result.getForecasts().get(0).getDescription().contains("API Error"));
        Assert.assertEquals(result.getForecasts().get(1).getSource(), "API Error");
        Assert.assertTrue(result.getForecasts().get(1).getDescription().contains("API Error"));
    }
    
    @Test
    public void testGetWeatherForecastWithDifferentDays() {
        // Test with 1 day
        WeatherService service = new WeatherService();
        WeatherData result1 = service.getWeatherForecast("Tokyo", 1);
        
        Assert.assertNotNull(result1);
        Assert.assertEquals(result1.getCity(), "Tokyo");
        
        // Test with 7 days
        WeatherData result7 = service.getWeatherForecast("Tokyo", 7);
        
        Assert.assertNotNull(result7);
        Assert.assertEquals(result7.getCity(), "Tokyo");
        
        // The 7-day forecast should have more or equal entries than the 1-day forecast
        // (allowing for API errors that might add entries)
        Assert.assertTrue(result7.getForecasts().size() >= result1.getForecasts().size(), 
            "7-day forecast should have more or equal entries than 1-day forecast");
    }
}