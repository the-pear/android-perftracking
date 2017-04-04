package com.rakuten.tech.mobile.perf.core;

import java.util.concurrent.atomic.AtomicReference;

public class Current {
    public final AtomicReference<Metric> metric = new AtomicReference<Metric>(null);
}
