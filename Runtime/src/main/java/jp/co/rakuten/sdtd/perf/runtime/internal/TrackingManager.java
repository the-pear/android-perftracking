package jp.co.rakuten.sdtd.perf.runtime.internal;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.rakuten.sdtd.perf.core.Config;
import jp.co.rakuten.sdtd.perf.core.Tracker;

/**
 * TrackingManager
 *
 * @author RMSDK team(prj-rmsdk@mail.rakuten.com)
 */
public class TrackingManager {
    public static TrackingManager INSTANCE = null;
    private Map<AggregatedData, Integer> mAggregatedDataMap;
    private Map<String, MeasurementData> mMeasurementDataMap;

    private TrackingManager() {
        mAggregatedDataMap = new HashMap<>();
        mMeasurementDataMap = new HashMap<>();
    }

    static void initialize(Context context, Config config) {
        Tracker.on(context, config);
        INSTANCE = new TrackingManager();
    }

    /**
     * Starts a new measurement.
     *
     * @param measurementId Measurement identifier.
     * @return trackingId
     */
    public void startMeasurement(String measurementId) {
        MeasurementData measurementData = mMeasurementDataMap.get(measurementId);
        if (measurementData != null) return;
        measurementData = new MeasurementData();
        measurementData.trackingId = Tracker.startCustom(measurementId);
        mMeasurementDataMap.put(measurementId, measurementData);
    }

    /**
     * Ends a measurement.
     *
     * @param measurementId Tracking ID returned from startCustom
     */
    public void endMeasurement(String measurementId) {
        MeasurementData measurementData = mMeasurementDataMap.get(measurementId);
        if (measurementData == null) return;
        Tracker.endCustom(measurementData.trackingId);
        mMeasurementDataMap.remove(measurementId);
    }

    /**
     * Starts a new aggregated measurement.
     *
     * @param id     Measurement identifier.
     * @param object Object associated with the measurement.
     */
    public void startAggregated(String id, Comparable object) {
        AggregatedData key = new AggregatedData(id, object);
        if (!mAggregatedDataMap.containsKey(key))
            mAggregatedDataMap.put(key, Tracker.startCustom(id));
    }

    /**
     * Ends a aggregated measurement.
     *
     * @param id     Measurement identifier.
     * @param object Object associated with the measurement.
     */
    public void endAggregated(String id, Comparable object) {
        AggregatedData key = new AggregatedData(id, object);
        if (mAggregatedDataMap.containsKey(key)) {
            Tracker.endCustom(mAggregatedDataMap.get(key));
            mAggregatedDataMap.remove(key);
        }
    }

    /**
     * Starts a new metric.
     *
     * @param metricId Metric ID, for example "launch", "search", "item"
     */
    public void startMetric(String metricId) {
        Tracker.startMetric(metricId);
    }

    private class MeasurementData {
        private int trackingId;
    }

    private class AggregatedData implements Comparable {
        private String measurementId;
        private Comparable object;

        private AggregatedData(String measurementId, Comparable object) {
            this.measurementId = measurementId;
            this.object = object;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof AggregatedData) {
                AggregatedData data = (AggregatedData) o;
                return data.measurementId.equals(measurementId) && data.object.equals(object);
            } else return false;
        }

        @Override
        public int hashCode() {
            return measurementId.hashCode() + object.hashCode();
        }

        @Override
        public int compareTo(@NonNull Object another) {
            if (another instanceof AggregatedData) {
                AggregatedData data = (AggregatedData) another;
                return data.measurementId.compareTo(measurementId) * 10000 + data.object.compareTo(object);
            }
            return -1;
        }
    }
}
