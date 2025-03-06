package com.cloudnineweather.model;

import java.util.List;

public class GoogleGeocodingResponse {
    private List<GoogleResult> results;
    private String status;

    public static class GoogleResult {
        private GoogleGeometry geometry;
        public GoogleGeometry getGeometry() { return geometry; }
        public void setGeometry(GoogleGeometry geometry) { this.geometry = geometry; }
    }

    public static class GoogleGeometry {
        private GoogleLocation location;
        public GoogleLocation getLocation() { return location; }
        public void setLocation(GoogleLocation location) { this.location = location; }
    }

    public static class GoogleLocation {
        private double lat;
        private double lng;
        public double getLat() { return lat; }
        public void setLat(double lat) { this.lat = lat; }
        public double getLng() { return lng; }
        public void setLng(double lng) { this.lng = lng; }
    }

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