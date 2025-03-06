package com.cloudnineweather.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WeatherDaily {
    private List<String> time;
    
    @JsonProperty("temperature_2m_max")
    private List<Double> temperature2mMax;
    
    @JsonProperty("temperature_2m_min")
    private List<Double> temperature2mMin;
    
    @JsonProperty("precipitation_probability_max")
    private List<Integer> precipitationProbabilityMax;

    public List<String> getTime() {
        return time;
    }

    public List<Double> getTemperature2mMax() {
        return temperature2mMax;
    }

    public List<Double> getTemperature2mMin() {
        return temperature2mMin;
    }

    public List<Integer> getPrecipitationProbabilityMax() {
        return precipitationProbabilityMax;
    }

    public void setTime(List<String> time) {
        this.time = time;
    }
}
