package com.cloudnineweather.service;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import com.cloudnineweather.model.GeoLocation;
import com.cloudnineweather.model.GoogleGeocodingResponse;

@ExtendWith(MockitoExtension.class)
public class GeocodingServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private GeocodingService geocodingService;

    @BeforeEach
    void setUp() {
        geocodingService = new GeocodingService(restTemplate);
    }

    @Test
    void getCoordinates_ValidAddress_ReturnsGeoLocation() {

        String address = "123 Main Street, Denver, CO 80202";
        GoogleGeocodingResponse mockResponse = createMockGeocodingResponse();
        when(restTemplate.getForObject(anyString(), eq(GoogleGeocodingResponse.class)))
            .thenReturn(mockResponse);

        GeoLocation location = geocodingService.getCoordinates(address);

        // Assert
        assertNotNull(location);
        assertEquals(40.7128, location.getLatitude());
        assertEquals(-74.006, location.getLongitude());
    }

    @Test
    void getCoordinates_InvalidAddress_ThrowsException() {
        String address = "Invalid Address";
        when(restTemplate.getForObject(anyString(), eq(GoogleGeocodingResponse.class)))
            .thenReturn(null);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            geocodingService.getCoordinates(address));
        assertEquals("Location not found: Invalid Address", exception.getMessage());
    }

    @Test
    void getCoordinates_WhenApiError_ThrowsException() {
        String address = "123 Main Street, Denver, CO 80202";
        when(restTemplate.getForObject(anyString(), eq(GoogleGeocodingResponse.class)))
            .thenThrow(new RuntimeException("Google API error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            geocodingService.getCoordinates(address));
        assertEquals("Google API error", exception.getMessage());
    }

    @Test
    void getCoordinates_WhenEmptyResults_ThrowsException() {
        String address = "123 Main Street, Denver, CO 80202";
        GoogleGeocodingResponse mockResponse = new GoogleGeocodingResponse();
        mockResponse.setStatus("OK");
        mockResponse.setResults(Arrays.asList());
        
        when(restTemplate.getForObject(anyString(), eq(GoogleGeocodingResponse.class)))
            .thenReturn(mockResponse);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            geocodingService.getCoordinates(address));
        assertEquals("Location not found: " + address, exception.getMessage());
    }

    @Test
    void getCoordinates_WhenInvalidStatus_ThrowsException() {
        String address = "123 Main Street, Denver, CO 80202";
        GoogleGeocodingResponse mockResponse = new GoogleGeocodingResponse();
        mockResponse.setStatus("INVALID");
        mockResponse.setResults(Arrays.asList());
        
        when(restTemplate.getForObject(anyString(), eq(GoogleGeocodingResponse.class)))
            .thenReturn(mockResponse);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            geocodingService.getCoordinates(address));
        assertEquals("Location not found: " + address, exception.getMessage());
    }

    private GoogleGeocodingResponse createMockGeocodingResponse() {
        GoogleGeocodingResponse response = new GoogleGeocodingResponse();
        response.setStatus("OK");
        response.setResults(Arrays.asList(new GoogleGeocodingResponse.GoogleResult()));
        
        GoogleGeocodingResponse.GoogleGeometry geometry = new GoogleGeocodingResponse.GoogleGeometry();
        GoogleGeocodingResponse.GoogleLocation location = new GoogleGeocodingResponse.GoogleLocation();
        location.setLat(40.7128);
        location.setLng(-74.0060);
        geometry.setLocation(location);
        response.getResults().get(0).setGeometry(geometry);
        
        return response;
    }
} 