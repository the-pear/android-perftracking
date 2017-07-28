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
    String country;
    private String region = null;

    EnvironmentInfo(Context context, CachingObservable<String> locationObservable) {

        locationObservable.addObserver(this);
        if (locationObservable.getCachedValue() != null) {
            this.update(locationObservable, locationObservable.getCachedValue());
        }

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

    }

    String getRegion() {
        synchronized (this) {
            return this.region;
        }
    }

    @Override
    public void update(Observable observable, Object value) {
        if (value instanceof String) {
            synchronized (this) {
                this.region = (String) value;
            }
        }
    }
}
