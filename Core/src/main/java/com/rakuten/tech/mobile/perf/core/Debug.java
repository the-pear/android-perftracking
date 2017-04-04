package com.rakuten.tech.mobile.perf.core;

import android.util.Log;

public class Debug {
    private final String TAG = "PERF";

    public void log(String msg) {
        Log.d(TAG, msg);
    }

    public void log(String action, Metric metric) {
        StringBuilder s = new StringBuilder();

        s.append(action).append(": ");
        s.append("metric=").append(metric.id);
        s.append(",startTime=").append(metric.startTime);
        s.append(",endTime=").append(metric.endTime);

        if (metric.endTime > metric.startTime) {
            s.append(",time=").append((metric.endTime - metric.startTime) / 1000000).append("ms");
        }

        s.append(",urls=").append(metric.urls);

        log(s.toString());
    }

    public void log(String action, Measurement m, String metricId) {
        StringBuilder s = new StringBuilder();

        s.append(action).append(": ");
        s.append("trackingId=").append(m.trackingId);
        s.append(",type=").append(m.type);

        if (m.a != null) {
            s.append(",a=").append(m.a);
        }

        if (m.b != null) {
            s.append(",b=").append(m.b);
        }

        s.append(",startTime=").append(m.startTime);
        s.append(",endTime=").append(m.endTime);

        if (m.endTime > m.startTime) {
            s.append(",time=").append((m.endTime - m.startTime) / 1000000).append("ms");
        }

        if (metricId != null) {
            s.append(",metric=").append(metricId);
        }

        log(s.toString());
    }
}

