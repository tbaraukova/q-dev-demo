package com.example.weather.controller;

import com.example.weather.model.WeatherData;
import com.example.weather.model.WeatherRequest;
import com.example.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class WeatherController {
    
    @Autowired
    private WeatherService weatherService;
    
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("weatherRequest", new WeatherRequest());
        return "index";
    }
    
    @PostMapping("/forecast")
    public String getForecast(@Valid WeatherRequest weatherRequest, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "index";
        }
        
        WeatherData weatherData = weatherService.getWeatherForecast(
            weatherRequest.getCity(), 
            Integer.parseInt(weatherRequest.getDays())
        );
        
        model.addAttribute("weatherData", weatherData);
        model.addAttribute("weatherRequest", weatherRequest);
        return "forecast";
    }
}