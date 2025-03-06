package com.cloudnineweather.service;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.client.RestTemplate;

import com.cloudnineweather.model.Current;
import com.cloudnineweather.model.ForecastResponse;
import com.cloudnineweather.model.GeoLocation;
import com.cloudnineweather.model.WeatherDaily;
import com.cloudnineweather.model.WeatherResponse;

@ExtendWith(MockitoExtension.class)
public class ForecastServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private GeocodingService geocodingService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private ForecastService forecastService;

    @BeforeEach
    void setUp() {
        forecastService = new ForecastService(restTemplate, geocodingService, redisTemplate);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void getForecast_WhenCached_ReturnsCachedResponse() {
        // Arrange
        String address = "123 Main Street, Denver, CO 80202";
        String zipcode = "80202";
        String cachedForecast = "Cached forecast data";
        when(valueOperations.get("weather_forecast::" + zipcode))
            .thenReturn(new ForecastResponse(cachedForecast, false));

        ForecastResponse response = forecastService.getForecast(address, zipcode);

        assertNotNull(response);
        assertEquals(cachedForecast, response.getForecast());
        assertTrue(response.isFromCache());
    }

    @Test
    void getForecast_WhenNotCached_FetchesNewData() {
        String address = "123 Main Street, Denver, CO 80202";
        String zipcode = "80202";
        GeoLocation location = new GeoLocation(40.7128, -74.006, address);
        WeatherResponse weatherResponse = createMockWeatherResponse();

        when(valueOperations.get("weather_forecast::" + zipcode)).thenReturn(null);
        when(geocodingService.getCoordinates(address)).thenReturn(location);
        when(restTemplate.getForObject(anyString(), eq(WeatherResponse.class)))
            .thenReturn(weatherResponse);
        doNothing().when(valueOperations).set(anyString(), any(ForecastResponse.class));

        ForecastResponse response = forecastService.getForecast(address, zipcode);

        assertNotNull(response);
        assertFalse(response.isFromCache());
        assertTrue(response.getForecast().contains("Current Temperature"));
    }

    @Test
    void getForecast_WhenError_ReturnsErrorResponse() {
        String address = "123 Main Street, Denver, CO 80202";
        String zipcode = "80202";
        when(valueOperations.get("weather_forecast::" + zipcode)).thenReturn(null);
        when(geocodingService.getCoordinates(address))
            .thenThrow(new RuntimeException("Location not found: " + address));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            forecastService.getForecast(address, zipcode));
        assertEquals("Location not found: " + address, exception.getMessage());
    }

    @Test
    void getForecast_WhenRedisError_ReturnsErrorResponse() {
        String address = "123 Main Street, Denver, CO 80202";
        String zipcode = "80202";
        when(valueOperations.get("weather_forecast::" + zipcode))
            .thenThrow(new RuntimeException("Redis connection error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            forecastService.getForecast(address, zipcode));
        assertEquals("Redis connection error", exception.getMessage());
    }

    @Test
    void getForecast_WhenWeatherApiError_ReturnsErrorResponse() {
        String address = "123 Main Street, Denver, CO 80202";
        String zipcode = "80202";
        GeoLocation location = new GeoLocation(40.7128, -74.006, address);

        when(valueOperations.get("weather_forecast::" + zipcode)).thenReturn(null);
        when(geocodingService.getCoordinates(address)).thenReturn(location);
        when(restTemplate.getForObject(anyString(), eq(WeatherResponse.class)))
            .thenThrow(new RuntimeException("Weather API error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            forecastService.getForecast(address, zipcode));
        assertEquals("Weather API error", exception.getMessage());
    }

    @Test
    void getForecast_WhenEmptyWeatherResponse_ReturnsErrorResponse() {
        String address = "123 Main Street, Denver, CO 80202";
        String zipcode = "80202";
        GeoLocation location = new GeoLocation(40.7128, -74.006, address);
        WeatherResponse emptyResponse = new WeatherResponse();

        when(valueOperations.get("weather_forecast::" + zipcode)).thenReturn(null);
        when(geocodingService.getCoordinates(address)).thenReturn(location);
        when(restTemplate.getForObject(anyString(), eq(WeatherResponse.class)))
            .thenReturn(emptyResponse);

        ForecastResponse response = forecastService.getForecast(address, zipcode);
        assertNotNull(response);
        assertFalse(response.isFromCache());
        assertTrue(response.getForecast().contains("Unable to fetch weather forecast"));
    }

    private WeatherResponse createMockWeatherResponse() {
        WeatherResponse response = new WeatherResponse();
        
        // Set up current weather
        Current current = new Current();
        current.setTemperature(72.5);
        current.setHumidity(45);
        response.setCurrent(current);

        // Set up daily forecast
        WeatherDaily daily = new WeatherDaily();
        daily.setTime(Arrays.asList("2024-03-06", "2024-03-07"));
        daily.setTemperature2mMax(Arrays.asList(75.0, 78.0));
        daily.setTemperature2mMin(Arrays.asList(65.0, 68.0));
        daily.setPrecipitationProbabilityMax(Arrays.asList(20, 30));
        response.setDaily(daily);

        return response;
    }
} 