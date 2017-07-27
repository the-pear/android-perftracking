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
 * LocationStore - Handles requesting location, response caching and publishing to observers.
 * <p>
 * Can be subscribed to location changes by creating an observer using `CachingObservable<String>` instance like,
 * `Observer MyTestObserver = new MyTestObserver(CachingObservable<String> locationObservable);
 * MyTestObserver(CachingObservable<String> locationObservable){
 * locationObservable.addObserver(this);
 * }`
 * <p>
 * Location is requested on every launch of the app i.e on starting at construction time and then hourly.
 * <p>
 * If location is already cached, while creating LocationStore instance store will emit cached location else no location will be emitted via its observable.
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
        getObservable().publish(readLocationFromCache());
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
                        getObservable().publish(newLocation.getRegionName());
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