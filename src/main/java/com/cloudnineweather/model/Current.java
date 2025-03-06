package com.cloudnineweather.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Current {
    @JsonProperty("temperature_2m")
    private double temperature_2m;
    @JsonProperty("relative_humidity_2m")
    private int relative_humidity_2m;

    public double getTemperature() {
        return temperature_2m;
    }

    public void setTemperature(double temperature_2m) {
        this.temperature_2m = temperature_2m;
    }

    public int getHumidity() {
        return relative_humidity_2m;
    }

    public void setHumidity(int relative_humidity_2m) {
        this.relative_humidity_2m = relative_humidity_2m;
    }

} 