package com.rakuten.tech.mobile.perf.runtime.internal;

import android.support.annotation.NonNull;

class TrackingData implements Comparable {
    private String measurementId;
    private Comparable object;

    TrackingData(String measurementId, Comparable object) {
        this.measurementId = measurementId;
        this.object = object;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TrackingData) {
            TrackingData data = (TrackingData) o;
            return measurementId.equals(data.measurementId) && nullSafeEquateObjects(object, data.object);
        } else {
            return false;
        }
    }

    private boolean nullSafeEquateObjects(Comparable one, Comparable two) {
        if (one != null && two != null) {
            return one.equals(two);
        }
        return one == null && two == null;
    }

    @Override
    public int hashCode() {
        if (object != null) {
            return measurementId.hashCode() + object.hashCode();
        } else {
            return measurementId.hashCode();
        }
    }

    @Override
    public int compareTo(@NonNull Object another) {
        if (another instanceof TrackingData) {
            TrackingData data = (TrackingData) another;
            if (measurementId.compareTo(data.measurementId) == 0) {
                return nullSafeCompareObjects(object, data.object);
            } else {
                return measurementId.compareTo(data.measurementId);
            }
        }
        return -1;
    }


    @SuppressWarnings("unchecked")
    private int nullSafeCompareObjects(Comparable one, Comparable two) {
        if (one == null && two == null) {
            return 0;
        }
        if (one == null ^ two == null) {
            return (one == null) ? -1 : 1;
        }
        return one.compareTo(two);
    }
}
