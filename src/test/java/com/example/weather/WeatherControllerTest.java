package com.example.weather;

import com.example.weather.controller.WeatherController;
import com.example.weather.model.WeatherData;
import com.example.weather.model.WeatherRequest;
import com.example.weather.service.WeatherService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
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

public class WeatherControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherController weatherController;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(weatherController).build();
    }

    @Test
    public void testIndexEndpoint() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("weatherRequest"));
    }

    @Test
    public void testGetForecastWithValidRequest() throws Exception {
        // Mock the service response
        List<WeatherData.DayForecast> forecasts = new ArrayList<>();
        forecasts.add(new WeatherData.DayForecast("2023-01-01", "20°C", "Sunny", "Test API"));
        WeatherData mockWeatherData = new WeatherData("London", forecasts);
        
        when(weatherService.getWeatherForecast(anyString(), anyInt())).thenReturn(mockWeatherData);

        mockMvc.perform(post("/forecast")
                .param("city", "London")
                .param("days", "3"))
                .andExpect(status().isOk())
                .andExpect(view().name("forecast"))
                .andExpect(model().attributeExists("weatherData"))
                .andExpect(model().attributeExists("weatherRequest"));
    }

    @Test
    public void testGetForecastWithInvalidCity() throws Exception {
        mockMvc.perform(post("/forecast")
                .param("city", "")  // Empty city name
                .param("days", "3"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().hasErrors());
    }

    @Test
    public void testGetForecastWithInvalidDays() throws Exception {
        mockMvc.perform(post("/forecast")
                .param("city", "London")
                .param("days", "5"))  // Invalid days value (not 1, 3, or 7)
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().hasErrors());
    }

    @Test
    public void testGetForecastWithInvalidCityFormat() throws Exception {
        mockMvc.perform(post("/forecast")
                .param("city", "London123")  // Invalid city format (contains numbers)
                .param("days", "3"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().hasErrors());
    }
}