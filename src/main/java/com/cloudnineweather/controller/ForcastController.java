package com.cloudnineweather.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/forecast")
public class ForcastController {

    @GetMapping("/{address}")
    public String getForecast(@PathVariable String address) {
        return "Here is your forcast for " + address;
    }
}