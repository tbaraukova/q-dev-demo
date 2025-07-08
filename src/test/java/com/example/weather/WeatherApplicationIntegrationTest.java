package com.example.weather;

import com.example.weather.model.WeatherData;
import com.example.weather.service.WeatherService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class WeatherApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @BeforeMethod
    public void setup() {
        // Create mock data
        List<WeatherData.DayForecast> forecasts = new ArrayList<>();
        forecasts.add(new WeatherData.DayForecast("2023-01-01", "20°C", "Sunny", "Test API"));
        forecasts.add(new WeatherData.DayForecast("2023-01-02", "22°C", "Partly Cloudy", "Test API"));
        forecasts.add(new WeatherData.DayForecast("2023-01-03", "18°C", "Rainy", "Test API"));
        
        WeatherData mockWeatherData = new WeatherData("London", forecasts);
        
        // Mock the service response
        when(weatherService.getWeatherForecast(anyString(), anyInt())).thenReturn(mockWeatherData);
    }

    @Test
    public void testApplicationFlow() throws Exception {
        // Test the index page
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("weatherRequest"));

        // Test submitting the form with valid data
        mockMvc.perform(post("/forecast")
                .param("city", "London")
                .param("days", "3"))
                .andExpect(status().isOk())
                .andExpect(view().name("forecast"))
                .andExpect(model().attributeExists("weatherData"))
                .andExpect(model().attributeExists("weatherRequest"));
    }

    @Test
    public void testFormValidation() throws Exception {
        // Test with empty city
        mockMvc.perform(post("/forecast")
                .param("city", "")
                .param("days", "3"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().hasErrors());

        // Test with invalid days
        mockMvc.perform(post("/forecast")
                .param("city", "London")
                .param("days", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().hasErrors());
    }

    @Test
    public void testContextLoads() {
        // This test will fail if the application context cannot be loaded
    }
}