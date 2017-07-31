package com.rakuten.tech.mobile.perf.core;

public class LocationData {
    public String getCountry() {
        return country;
    }

    public String getRegion() {
        return region;
    }

    private final String country;
    private final String region;

    public LocationData(String country, String region) {
        this.country = country;
        this.region = region;
    }
}