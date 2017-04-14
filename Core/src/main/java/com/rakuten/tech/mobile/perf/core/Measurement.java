package com.rakuten.tech.mobile.perf.core;

public class Measurement {

    public static final byte METRIC = 1;
    public static final byte METHOD = 2;
    public static final byte URL = 3;
    public static final byte CUSTOM = 4;

    public static final long MAX_TIME = 30000000000L; // 30 s

    public int trackingId;
    public byte type;
    public Object a;
    public Object b;
    public long startTime;
    public long endTime;

    public void clear() {
        trackingId = 0;
        type = 0;
        a = null;
        b = null;
        startTime = 0;
        endTime = 0;
    }
}
