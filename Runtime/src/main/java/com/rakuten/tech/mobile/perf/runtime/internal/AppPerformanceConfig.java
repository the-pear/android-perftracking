package com.rakuten.tech.mobile.perf.runtime.internal;

/**
 * Configuration to control the tracker initialization.
 * Configuration changes dynamically based on instrumentation switch.
 */
final class AppPerformanceConfig {
    // enabled cannot be final, as it won't allow us to change value dynamically.
    public static boolean enabled = true;
}
