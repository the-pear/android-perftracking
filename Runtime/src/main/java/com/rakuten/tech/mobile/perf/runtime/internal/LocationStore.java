package com.rakuten.tech.mobile.perf.runtime.internal;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

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

    private Context mContext;
    private RequestQueue mRequestQueue;
    private String mSubScriptionKey;
    private String mUrlPrefix;
    private Handler handler;

    LocationStore(Context context, RequestQueue requestQueue, String subScriptionKey, String urlPrefix) {
        mContext = context;
        mRequestQueue = requestQueue;
        mSubScriptionKey = subScriptionKey;
        mUrlPrefix = urlPrefix;
        String cachedLocation = readLocationFromCache();
        observable = new CachingObservable<>(cachedLocation);
        handler = new Handler(Looper.getMainLooper());
        startLoadingLocationPeriodically();
    }

    private final Runnable periodicLocationCheck = new Runnable() {
        public void run() {
            if (Tracker.isTrackerRunning()) {
                handler.postDelayed(this, TIME_INTERVAL);
                loadLocationFromApi();
            }
        }
    };

    private void startLoadingLocationPeriodically() {
        loadLocationFromApi();
        handler.postDelayed(periodicLocationCheck, TIME_INTERVAL);
    }

    private void loadLocationFromApi() {

        if (mSubScriptionKey == null)
            Log.d(TAG, "Cannot read metadata `com.rakuten.tech.mobile.perf.SubscriptionKey` from manifest, automated performance tracking will not work.");

        new GeoLocationRequest(mUrlPrefix,
                mSubScriptionKey,
                new Response.Listener<GeoLocationResult>() {
                    @Override
                    public void onResponse(GeoLocationResult newLocation) {
                        writeLocationToCache(newLocation.getRegionName());
                        observable.publish(newLocation.getRegionName());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Throwable throwable = error;
                String message = error.getClass().getName();
                while (throwable.getMessage() == null && throwable.getCause() != null)
                    throwable = throwable.getCause();
                if (throwable.getMessage() != null) message = throwable.getMessage();
                Log.d(TAG, "Error: " + message);
            }
        }).queue(mRequestQueue);
    }

    private void writeLocationToCache(String locationName) {
        if (mContext != null) {
            mContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(LOCATION_KEY, locationName).apply();
        }
    }

    @Nullable
    public String readLocationFromCache() {
        String result = null;
        if (mContext != null) {
            result = mContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .getString(LOCATION_KEY, null);
        }
        return result;
    }

}