package com.rakuten.tech.mobile.perf.runtime;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class StandardMetricSpec extends RobolectricUnitSpec {
    @Test public void shouldConvertStringToEnum() {
        assertThat(StandardMetric.of("_launch")).isEqualTo(StandardMetric.LAUNCH);
        assertThat(StandardMetric.of("_search")).isEqualTo(StandardMetric.SEARCH);
        assertThat(StandardMetric.of("_item")).isEqualTo(StandardMetric.ITEM);
        assertThat(StandardMetric.of("anything else")).isNull();
    }
}
