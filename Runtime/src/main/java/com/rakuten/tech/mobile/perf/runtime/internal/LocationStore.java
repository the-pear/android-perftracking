package com.rakuten.tech.mobile.perf.runtime.internal;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.rakuten.tech.mobile.perf.core.Tracker;

/**
 * LocationStore is an observable for location. This class handles requesting location, response caching and publishing to observers.
 */
class LocationStore extends Store<String> {
    private final static String TAG = LocationStore.class.getSimpleName();
    private static final String PREFS = "app_performance";
    private static final String LOCATION_KEY = "location_key";
    private static final int TIME_INTERVAL = 60 * 60 * 1000; // 1 HOUR in milli seconds

    private final RequestQueue requestQueue;
    private final String subscriptionKey;
    private final String urlPrefix;
    private final SharedPreferences prefs;
    private Handler handler;

    LocationStore(Context context, RequestQueue requestQueue, String subscriptionKey, String urlPrefix) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        this.requestQueue = requestQueue;
        this.subscriptionKey = subscriptionKey;
        this.urlPrefix = urlPrefix;
        observable.publish(readLocationFromCache());
        handler = new Handler(Looper.getMainLooper());
        loadLocationFromApi();
        handler.postDelayed(periodicLocationCheck, TIME_INTERVAL);
    }

    private final Runnable periodicLocationCheck = new Runnable() {
        public void run() {
            if (Tracker.isTrackerRunning()) {
                handler.postDelayed(this, TIME_INTERVAL);
                loadLocationFromApi();
            }
        }
    };

    private void loadLocationFromApi() {

        if (subscriptionKey == null) {
            Log.d(TAG, "Cannot read metadata `com.rakuten.tech.mobile.perf.SubscriptionKey` from manifest, automated performance tracking will not work.");
        }
        new GeoLocationRequest(urlPrefix,
                subscriptionKey,
                new Response.Listener<GeoLocationResult>() {
                    @Override
                    public void onResponse(GeoLocationResult newLocation) {
                        writeLocationToCache(newLocation.getRegionName());
                        observable.publish(newLocation.getRegionName());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error loading location", error);
            }
        }).queue(requestQueue);
    }

    private void writeLocationToCache(String locationName) {
        if (prefs != null) {
            prefs.edit().putString(LOCATION_KEY, locationName).apply();
        }
    }

    @Nullable
    private String readLocationFromCache() {
        return prefs != null ? prefs.getString(LOCATION_KEY, null) : null;
    }

}