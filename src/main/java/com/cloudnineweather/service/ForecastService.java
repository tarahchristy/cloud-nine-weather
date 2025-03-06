package com.cloudnineweather.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.cloudnineweather.model.ForecastResponse;
import com.cloudnineweather.model.GeoLocation;
import com.cloudnineweather.model.WeatherDaily;
import com.cloudnineweather.model.WeatherResponse;

@Service
public class ForecastService {

    private final RestTemplate restTemplate;
    private final GeocodingService geocodingService;
    private final RedisTemplate<String, Object> redisTemplate;

    public ForecastService(RestTemplate restTemplate, GeocodingService geocodingService, RedisTemplate<String, Object> redisTemplate) {
        this.restTemplate = restTemplate;
        this.geocodingService = geocodingService;
        this.redisTemplate = redisTemplate;
    }
    
    public ForecastResponse getForecast(String address, String zipcode) {
        // Check if forecast exists in Redis cache
        ForecastResponse cachedResponse = (ForecastResponse) redisTemplate.opsForValue().get("weather_forecast::" + zipcode);
        if (cachedResponse != null) {
            return new ForecastResponse(cachedResponse.getForecast(), true);
        }
        return fetchWeather(address, zipcode);
    }

    //Using zipcode as the key to cache the forecast
    private ForecastResponse fetchWeather(String address, String zipcode) {
        // Get coordinates from address
        GeoLocation location = geocodingService.getCoordinates(address);
        
        String url = String.format(
            "https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f" +
            "&current=temperature_2m,relative_humidity_2m" + 
            "&daily=temperature_2m_max,temperature_2m_min,precipitation_probability_max" +
            "&temperature_unit=fahrenheit",
            location.getLatitude(),
            location.getLongitude()
        );
        
        try {
            WeatherResponse response = restTemplate.getForObject(url, WeatherResponse.class);
            String forecast = formatResponse(response);
            ForecastResponse forecastResponse = new ForecastResponse(forecast, false);
            // Store the entire ForecastResponse object
            redisTemplate.opsForValue().set("weather_forecast::" + zipcode, forecastResponse);
            return forecastResponse;
        } catch (RestClientException | IllegalArgumentException e) {
            return new ForecastResponse("Error fetching weather forecast: " + e.getMessage(), false);
        }
    }

    private String formatResponse(WeatherResponse response) {

        if (response == null || response.getDaily() == null || response.getCurrent() == null) {
            return "Unable to fetch weather forecast";
        }

        StringBuilder forecast = new StringBuilder();
        
        // Add current conditions
        forecast.append(String.format(
            "Current Temperature: %.1f°F (Humidity: %d%%)\n\n",
            response.getCurrent().getTemperature(),
            response.getCurrent().getHumidity()
        ));
        
        // Add 7-day forecast
        forecast.append("7-day forecast:\n");
        WeatherDaily daily = response.getDaily();
        
        for (int i = 0; i < daily.getTime().size(); i++) {
            forecast.append(String.format(
                "%s: High: %.1f°F, Low: %.1f°F, Precipitation: %d%%\n",
                daily.getTime().get(i),
                daily.getTemperature2mMax().get(i),
                daily.getTemperature2mMin().get(i),
                daily.getPrecipitationProbabilityMax().get(i)
            ));
        }

        return forecast.toString();
    }

    @CacheEvict(value = "weather_forecast", key = "#zipcode")
    public void clearForecast(String zipcode) {
        // This method is intentionally left blank
    }

    @CachePut(value = "weather_forecast", key = "#zipcode")
    public ForecastResponse updateForecast(String address, String zipcode) {
        return getForecast(address, zipcode);
    }

}
