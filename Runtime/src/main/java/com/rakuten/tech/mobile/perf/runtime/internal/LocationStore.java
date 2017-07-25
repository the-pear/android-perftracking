package com.rakuten.tech.mobile.perf.runtime.internal;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.rakuten.tech.mobile.perf.core.CachingObservable;
import com.rakuten.tech.mobile.perf.core.Tracker;

/**
 * LocationStore is an observable for location. This class handles requesting location, response caching and publishing to observers.
 */
class LocationStore extends Store<String> {
    private final static String TAG = LocationStore.class.getSimpleName();
    private static final String PREFS = "app_performance";
    private static final String LOCATION_KEY = "location_key";
    private static final int TIME_INTERVAL = 60 * 60 * 1000; // 1 HOUR in milli seconds

    private final RequestQueue mRequestQueue;
    private final String mSubscriptionKey;
    private final String mUrlPrefix;
    private final SharedPreferences mPrefs;
    private Handler mHandler;

    LocationStore(Context context, RequestQueue requestQueue, String subscriptionKey, String urlPrefix) {
        mPrefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        mRequestQueue = requestQueue;
        mSubscriptionKey = subscriptionKey;
        mUrlPrefix = urlPrefix;
        observable = new CachingObservable<>(readLocationFromCache());
        mHandler = new Handler(Looper.getMainLooper());
        startLoadingLocationPeriodically();
    }

    private final Runnable periodicLocationCheck = new Runnable() {
        public void run() {
            if (Tracker.isTrackerRunning()) {
                mHandler.postDelayed(this, TIME_INTERVAL);
                loadLocationFromApi();
            }
        }
    };

    private void startLoadingLocationPeriodically() {
        loadLocationFromApi();
        mHandler.postDelayed(periodicLocationCheck, TIME_INTERVAL);
    }

    private void loadLocationFromApi() {

        if (mSubscriptionKey == null)
            Log.d(TAG, "Cannot read metadata `com.rakuten.tech.mobile.perf.SubscriptionKey` from manifest, automated performance tracking will not work.");

        new GeoLocationRequest(mUrlPrefix,
                mSubscriptionKey,
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
        }).queue(mRequestQueue);
    }

    private void writeLocationToCache(String locationName) {
        if (mPrefs != null) {
            mPrefs.edit().putString(LOCATION_KEY, locationName).apply();
        }
    }

    @Nullable
    private String readLocationFromCache() {
        String result = null;
        if (mPrefs != null) {
            result = mPrefs.getString(LOCATION_KEY, null);
        }
        return result;
    }

}