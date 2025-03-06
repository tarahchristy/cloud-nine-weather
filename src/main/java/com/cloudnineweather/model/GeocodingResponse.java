package com.cloudnineweather.model;

import java.util.List;

public class GeocodingResponse {
    private List<GeoLocation> results;

    public List<GeoLocation> getResults() {
        return results;
    }

    public void setResults(List<GeoLocation> results) {
        this.results = results;
    }
}