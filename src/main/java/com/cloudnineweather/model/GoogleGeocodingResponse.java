package com.cloudnineweather.model;

import java.util.List;

public class GoogleGeocodingResponse {
    private List<GoogleResult> results;
    private String status;

    public List<GoogleResult> getResults() {
        return results;
    }

    public void setResults(List<GoogleResult> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 