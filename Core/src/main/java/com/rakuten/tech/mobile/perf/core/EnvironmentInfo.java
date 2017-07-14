package com.rakuten.tech.mobile.perf.core;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

class EnvironmentInfo implements Observer {
    String device;
    String country;
    String network;
    private Observable ov = null;
    private static EnvironmentInfo info;

    EnvironmentInfo(Observable ov){
        this.ov = ov;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public static EnvironmentInfo get(Context context, Observable observable) {
        info = new EnvironmentInfo(observable);

        info.device = Build.MODEL;

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            info.country = tm.getSimCountryIso();
            info.network = tm.getNetworkOperatorName();
        }

        if (info.country == null || "".equals(info.country)) {
            info.country = Locale.getDefault().getCountry();
        }

        if (info.country != null) {
            info.country = info.country.toLowerCase();
        }

        if (info.network == null || "".equals(info.network)) {
            info.network = "wifi";
        }

        return info;
    }

    @Override
    public void update(Observable observable, Object o) {
        if(ov == observable) {
            info.setCountry(((ObservableLocation) observable).getValue());
        }
    }
}
