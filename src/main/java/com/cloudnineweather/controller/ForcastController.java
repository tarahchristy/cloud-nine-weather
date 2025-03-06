package com.cloudnineweather.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloudnineweather.model.ForecastResponse;
import com.cloudnineweather.service.ForcastService;

@RestController
@RequestMapping("/forecast")
public class ForcastController {

    private final ForcastService forcastService;

    @Autowired
    public ForcastController(ForcastService forcastService) {
        this.forcastService = forcastService;
    }

    @GetMapping
    public ForecastResponse getForecast(
        @RequestParam String street,
        @RequestParam String city,
        @RequestParam String state,
        @RequestParam String zipcode
    ) {
        String address = String.format("%s, %s, %s %s", street, city, state, zipcode);
        return forcastService.getForcast(address);
    }
}