package com.rakuten.tech.mobile.perf.runtime.internal;

/**
 * GeoLocation Result
 */

class GeoLocationResult {
    private String regionName;

    GeoLocationResult(String name) {
        regionName = name;
    }

    public String getRegionName() {
        return regionName;
    }
}
