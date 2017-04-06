package com.rakuten.tech.mobile.perf.runtime;

/**
 * Standard Metric
 *
 */

public enum StandardMetric {
    LAUNCH("_launch"),
    SEARCH("_search"),
    ITEM("_item");

    private final String mValue;

    StandardMetric(final String value) {
        this.mValue = value;
    }

    public static StandardMetric of(final String value) {
        for (StandardMetric x : values()) {
            if (x.mValue.equals(value)) return x;
        }
        return null;
    }

    public String getValue() {
        return mValue;
    }
}
