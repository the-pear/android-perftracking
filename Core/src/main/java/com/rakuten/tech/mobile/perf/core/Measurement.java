package com.rakuten.tech.mobile.perf.core;

public class Measurement {

    static final byte METRIC = 1;
    static final byte METHOD = 2;
    static final byte URL = 3;
    static final byte CUSTOM = 4;

    static final long MAX_TIME = 30000L; // 30 s

    int trackingId;
    byte type;
    Object a;
    Object b;
    long startTime;
    long endTime;

    void clear() {
        trackingId = 0;
        type = 0;
        a = null;
        b = null;
        startTime = 0;
        endTime = 0;
    }
}
