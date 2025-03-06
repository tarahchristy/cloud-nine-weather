package com.cloudnineweather.model;

public class WeatherResponse {
    private WeatherDaily daily;
    private Current current;

    public WeatherDaily getDaily() {
        return daily;
    }

    public void setDaily(WeatherDaily daily) {
        this.daily = daily;
    }

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }
}

