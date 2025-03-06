package com.cloudnineweather.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloudnineweather.model.ForecastResponse;
import com.cloudnineweather.service.ForecastService;

@RestController
@RequestMapping("/api")
public class ForecastController {

    private final ForecastService forecastService;

    public ForecastController(ForecastService forecastService) {
        this.forecastService = forecastService;
    }

    @GetMapping("/forecast")
    public ForecastResponse getForecast(
        @RequestParam String street,
        @RequestParam String city,
        @RequestParam String state,
        @RequestParam String zipcode
    ) {
        String address = String.format("%s, %s, %s %s", street, city, state, zipcode);
        ForecastResponse forecast = forecastService.getForecast(address, zipcode);
        return forecast;
    }
}