package com.cloudnineweather.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cloudnineweather.model.GeoLocation;
import com.cloudnineweather.model.GoogleGeocodingResponse;

@Service
public class GeocodingService {

    private final RestTemplate restTemplate;
    
    @Value("${google.maps.api.key}")
    private String apiKey;

    @Autowired
    public GeocodingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public GeoLocation getCoordinates(String address) {
        String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
        String url = String.format(
            "https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s",
            encodedAddress, apiKey
        );
        
        GoogleGeocodingResponse response = restTemplate.getForObject(url, GoogleGeocodingResponse.class);
        if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
            throw new RuntimeException("Location not found: " + address);
        }
        
        double latitude = response.getResults().get(0).getGeometry().getLocation().getLat();
        double longitude = response.getResults().get(0).getGeometry().getLocation().getLng();

        return new GeoLocation(latitude, longitude, address);
    }
} 