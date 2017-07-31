package com.rakuten.tech.mobile.perf.core;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

class EnvironmentInfo implements Observer {
    String device;
    String network;
    private String country = null;
    private String region = null;

    EnvironmentInfo(Context context, CachingObservable<LocationData> locationObservable) {

        locationObservable.addObserver(this);

        this.device = Build.MODEL;

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            this.country = tm.getSimCountryIso();
            this.network = tm.getNetworkOperatorName();
        }

        if (this.country == null || "".equals(this.country)) {
            this.country = Locale.getDefault().getCountry();
        }

        if (this.country != null) {
            this.country = this.country.toLowerCase();
        }

        if (this.network == null || "".equals(this.network)) {
            this.network = "wifi";
        }

        if (locationObservable.getCachedValue() != null) {
            this.update(locationObservable, locationObservable.getCachedValue());
        }

    }

    String getCountry() {
        synchronized (this) {
            return this.country;
        }
    }

    String getRegion() {
        synchronized (this) {
            return this.region;
        }
    }

    @Override
    public void update(Observable observable, Object value) {
        if (value instanceof LocationData) {
            synchronized (this) {
                this.region = ((LocationData) value).getRegion();
                this.country = ((LocationData) value).getCountry();
            }
        }
    }
}
