package com.cloudnineweather.model;

public class ForecastResponse {
    private String forecast;
    private boolean fromCache;

    // Required for Redis serialization
    public ForecastResponse() {}

    public ForecastResponse(String forecast, boolean fromCache) {
        this.forecast = forecast;
        this.fromCache = fromCache;
    }

    public String getForecast() {
        return forecast;
    }

    public void setForecast(String forecast) {
        this.forecast = forecast;
    }

    public boolean isFromCache() {
        return fromCache;
    }

    public void setFromCache(boolean fromCache) {
        this.fromCache = fromCache;
    }
} 