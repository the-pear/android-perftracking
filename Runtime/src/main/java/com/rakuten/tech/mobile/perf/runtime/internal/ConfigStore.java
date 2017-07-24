package com.rakuten.tech.mobile.perf.runtime.internal;


import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.rakuten.tech.mobile.perf.R;
import com.rakuten.tech.mobile.perf.core.CachingObservable;
import com.rakuten.tech.mobile.perf.core.Tracker;

import java.util.Random;

/**
 * ConfigStore - Handles requesting config, response caching and publishing to observers.
 */
class ConfigStore extends Store<ConfigurationResult> {
    private final static String TAG = ConfigStore.class.getSimpleName();
    private static final String PREFS = "app_performance";
    private static final String CONFIG_KEY = "config_key";
    private static final int TIME_INTERVAL = 60 *60 * 1000; // 1 HOUR in milli seconds

    private Context mContext;
    private RequestQueue mRequestQueue;
    private String mSubScriptionKey;
    private String mUrlPrefix;
    private Handler handler;

    ConfigStore(Context context, RequestQueue requestQueue, String subScriptionKey, String urlPrefix) {
        mContext = context;
        mRequestQueue = requestQueue;
        mSubScriptionKey = subScriptionKey;
        mUrlPrefix = urlPrefix;
        ConfigurationResult cachedConfig = readConfigFromCache();
        observable = new CachingObservable<>(cachedConfig);
        handler = new Handler(Looper.getMainLooper());
        startLoadingConfigPeriodically();
    }

    private void startLoadingConfigPeriodically() {
        loadConfigurationFromApi();
        handler.postDelayed(periodicCheck, TIME_INTERVAL);
    }

    private final Runnable periodicCheck = new Runnable() {
        public void run() {
            if (Tracker.isTrackerRunning()) {
                handler.postDelayed(this, TIME_INTERVAL);
                loadConfigurationFromApi();
            }
        }
    };

    private void loadConfigurationFromApi() {
        ConfigurationParam param = null;

        PackageManager packageManager = mContext.getPackageManager();
        String packageName = mContext.getPackageName();
        try {
            param = new ConfigurationParam.Builder()
                    .setAppId(packageName)
                    .setAppVersion(packageManager.getPackageInfo(packageName, 0).versionName)
                    .setCountryCode(mContext.getResources().getConfiguration().locale.getCountry())
                    .setPlatform("android")
                    .setSdkVersion(mContext.getResources().getString(R.string.perftracking__version))
                    .build();
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, e.getMessage());
        }

        if (mSubScriptionKey == null)
            Log.d(TAG, "Cannot read metadata `com.rakuten.tech.mobile.perf.SubscriptionKey` from manifest, automated performance tracking will not work.");

        if (param != null) {
            new ConfigurationRequest(mUrlPrefix,
                    mSubScriptionKey,
                    param, new Response.Listener<ConfigurationResult>() {
                @Override
                public void onResponse(ConfigurationResult newConfig) {
                    if (newConfig == null && Tracker.isTrackerRunning() == true) {
                        TrackingManager.deinitialize();
                    }

                    ConfigurationResult prevConfig = readConfigFromCache();
                    boolean shouldRollDice = (newConfig != null && Tracker.isTrackerRunning() == true && prevConfig == null)
                            || (prevConfig != null && newConfig != null && newConfig.getEnablePercent() < prevConfig.getEnablePercent());

                    if (shouldRollDice) {
                        double randomNumber = new Random(System.currentTimeMillis()).nextDouble() * 100.0;
                        if (randomNumber > newConfig.getEnablePercent()) {
                            // DeInitialize Tracking Manager
                            TrackingManager.deinitialize();
                        }
                    }
                    writeConfigToCache(newConfig);
                    observable.publish(newConfig);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // DeInitialize Tracking Manager as we couldn't able to get new config from api
                    TrackingManager.deinitialize();

                    Throwable throwable = error;
                    String message = error.getClass().getName();
                    while (throwable.getMessage() == null && throwable.getCause() != null)
                        throwable = throwable.getCause();
                    if (throwable.getMessage() != null) message = throwable.getMessage();
                    Log.d(TAG, "Error: " + message);
                }
            }).queue(mRequestQueue);
        }
    }

    public void writeConfigToCache(ConfigurationResult result) {
        if (mContext != null) {
            mContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(CONFIG_KEY, new Gson().toJson(result)).apply();
        }
    }

    @Nullable
    public ConfigurationResult readConfigFromCache() {
        String result = null;
        if (mContext != null) {
            result = mContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .getString(CONFIG_KEY, null);
        }
        return result != null ? new Gson().fromJson(result, ConfigurationResult.class) : null;
    }
}