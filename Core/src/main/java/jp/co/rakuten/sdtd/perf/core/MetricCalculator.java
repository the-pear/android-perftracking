package jp.co.rakuten.sdtd.perf.core;

public class MetricCalculator {
    private final long LINGER_PERIOD = 200000000L; // 200 ms

    private final MeasurementBuffer _buffer;

    public MetricCalculator(MeasurementBuffer buffer)
    {
        _buffer = buffer;
    }

    public boolean calculate(Measurement metricMeasurement, int startIndex, int endIndex)
    {
        long metricStartTime = metricMeasurement.startTime;
        long metricEndTime = metricStartTime;
        int urls = 0;
        boolean anotherMetricStarted = false;
        long now = System.nanoTime();

        for (int i = startIndex; i != endIndex; i = (i + 1) % MeasurementBuffer.SIZE) {
            Measurement m = _buffer.at[i];

            if (m.trackingId == 0)
            {
                break;
            }

            if (m.type == Measurement.METRIC) {
                anotherMetricStarted = true;
                break;
            }

            long startTime = m.startTime;

            if (startTime > metricEndTime + LINGER_PERIOD)
            {
                break;
            }

            long endTime = m.endTime;
            if (endTime == 0) {
                if (now - startTime > Measurement.TIMEOUT) {
                    continue;
                }

                return false;
            }

            if (endTime - startTime > Measurement.TIMEOUT) {
                continue;
            }

            if (endTime > metricEndTime) {
                metricEndTime = endTime;
            }

            if (m.type == Measurement.URL) {
                urls++;
            }
        }

        if (!anotherMetricStarted) {
            if (now - metricEndTime < LINGER_PERIOD) {
                return false;
            }
        }

        Metric metric = (Metric)metricMeasurement.a;
        metricMeasurement.endTime = metricEndTime;
        metric.urls = urls;

        return true;
    }
}
