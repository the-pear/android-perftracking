package com.rakuten.tech.mobile.perf.runtime.internal;

class GeoLocationResult {
    private final String regionName;

    GeoLocationResult(String name) {
        regionName = name;
    }

    String getRegionName() {
        return regionName;
    }
}
