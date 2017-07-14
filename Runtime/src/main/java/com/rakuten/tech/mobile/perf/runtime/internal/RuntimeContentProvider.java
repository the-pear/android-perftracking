package com.rakuten.tech.mobile.perf.runtime.internal;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;
import com.google.gson.Gson;
import com.rakuten.tech.mobile.perf.R;
import com.rakuten.tech.mobile.perf.core.Config;
import com.rakuten.tech.mobile.perf.core.ObservableLocation;
import com.rakuten.tech.mobile.perf.core.Tracker;
import com.rakuten.tech.mobile.perf.runtime.Metric;
import com.rakuten.tech.mobile.perf.runtime.StandardMetric;

import java.util.Locale;
import java.util.Random;


/**
 * RuntimeContentProvider - a custom high-priority ContentProvider, to start tracking early in the process launch phase.
 */

public class RuntimeContentProvider extends ContentProvider {
    private static final String TAG = RuntimeContentProvider.class.getSimpleName();
    private static final String PREFS = "app_performance";
    private static final String CONFIG_KEY = "config_key";
    private static final String LOCATION_KEY = "location_key";
    private static final int TIME_INTERVAL = 60 * 60 * 1000; // 1 HOUR in milli seconds

    private Handler handler;
    private Context mContext;
    private RequestQueue mQueue;
    private ObservableLocation locationObservable;

    @Override
    public boolean onCreate() {
        locationObservable = new ObservableLocation(null);
        mContext = getContext();
        if (mContext == null) return false;
        if (!AppPerformanceConfig.enabled) return false; // Return when instrumentation is disabled

        mQueue = new RequestQueue(new NoCache(), new BasicNetwork(new HurlStack()));
        mQueue.start();

        // Load data from last configuration
        ConfigurationResult lastConfig = readConfigFromCache();
        if(readLocationFromCache() != null) {
            locationObservable.setValue(readLocationFromCache());
        }
        Config config = createConfig(mContext, lastConfig);
        if (config != null) {
            // Initialise Tracking Manager
            TrackingManager.initialize(mContext, config, locationObservable); // TODO Config class should be a builder and have all the values set properly
            Metric.start(StandardMetric.LAUNCH.getValue());
        }
        // Get latest configuration
        loadConfigurationFromApi(mContext, mQueue);
        // Get latest Location
        loadLocationFromApi(mQueue);

        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(periodicCheck, TIME_INTERVAL);
        handler.postDelayed(periodicLocationCheck, TIME_INTERVAL);
        return false;
    }


    private final Runnable periodicCheck = new Runnable() {
        public void run() {
            if (Tracker.isTrackerRunning()) {
                handler.postDelayed(this, TIME_INTERVAL);
                loadConfigurationFromApi(mContext, mQueue);
            }
        }
    };

    private final Runnable periodicLocationCheck = new Runnable() {
        public void run() {
            if (Tracker.isTrackerRunning()) {
                handler.postDelayed(this, TIME_INTERVAL);
                loadLocationFromApi(mQueue);
            }
        }
    };

    private void loadConfigurationFromApi(Context context, RequestQueue queue) {
        ConfigurationParam param = null;

        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        try {
            param = new ConfigurationParam.Builder()
                    .setAppId(packageName)
                    .setAppVersion(packageManager.getPackageInfo(packageName, 0).versionName)
                    .setCountryCode(context.getResources().getConfiguration().locale.getCountry())
                    .setPlatform("android")
                    .setSdkVersion(context.getResources().getString(R.string.perftracking__version))
                    .build();
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, e.getMessage());
        }

        String subscriptionKey = getMetaData("com.rakuten.tech.mobile.perf.SubscriptionKey");

        if (subscriptionKey == null)
            Log.d(TAG, "Cannot read metadata `com.rakuten.tech.mobile.perf.SubscriptionKey` from manifest, automated performance tracking will not work.");

        if (param != null) {
            new ConfigurationRequest(getMetaData("com.rakuten.tech.mobile.perf.ConfigurationUrlPrefix"),
                    subscriptionKey,
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
            }).queue(queue);
        }
    }

    private void loadLocationFromApi(RequestQueue queue) {

        // geo location dev environment - subscription key
        String subscriptionKey = getMetaData("com.rakuten.tech.mobile.perf.geo.SubscriptionKey");
        if (subscriptionKey == null)
            Log.d(TAG, "Cannot read metadata `com.rakuten.tech.mobile.perf.geo.SubscriptionKey` from manifest, automated performance tracking will not work.");

        new GeoLocationRequest(null,
                subscriptionKey,
                new Response.Listener<GeoLocationResult>() {
                    @Override
                    public void onResponse(GeoLocationResult newLocation) {
                        writeLocationToCache(newLocation.getRegionName());
                        locationObservable.setValue(newLocation.getRegionName());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Throwable throwable = error;
                String message = error.getClass().getName();
                while (throwable.getMessage() == null && throwable.getCause() != null)
                    throwable = throwable.getCause();
                if (throwable.getMessage() != null) message = throwable.getMessage();
                writeLocationToCache(Locale.getDefault().getCountry());
                locationObservable.setValue(Locale.getDefault().getCountry());
            }
        }).queue(queue);
    }

    /**
     * Configuration for {@link TrackingManager}
     *
     * @param context    application context
     * @param lastConfig cached config, may be null
     * @return Configuration for {@link TrackingManager}, may be null
     */
    @Nullable
    private Config createConfig(@NonNull Context context, @Nullable ConfigurationResult lastConfig) {
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        if (lastConfig == null) return null;
        Config config = null; // configuration for TrackingManager

        double enablePercent = lastConfig.getEnablePercent();

        double randomNumber = new Random(System.currentTimeMillis()).nextDouble() * 100.0;
        if (randomNumber <= enablePercent) {
            config = new Config();
            config.app = packageName;
            try {
                config.version = packageManager
                        .getPackageInfo(packageName, 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                Log.d(TAG, e.getMessage());
            }
            try {
                ApplicationInfo ai = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                Bundle bundle = ai.metaData;
                config.debug = bundle.getBoolean("com.rakuten.tech.mobile.perf.debug");
            } catch (PackageManager.NameNotFoundException | NullPointerException e) {
                config.debug = false;
            }
            config.eventHubUrl = lastConfig.getSendUrl();
            config.header = lastConfig.getHeader();
        }

        return config;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private void writeConfigToCache(ConfigurationResult result) {
        if (getContext() != null) {
            getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(CONFIG_KEY, new Gson().toJson(result)).apply();
        }
    }

    @Nullable
    private ConfigurationResult readConfigFromCache() {
        Context context = getContext();
        String result = null;
        if (context != null) {
            result = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .getString(CONFIG_KEY, null);
        }
        return result != null ? new Gson().fromJson(result, ConfigurationResult.class) : null;
    }

    private void writeLocationToCache(String locationName) {
        if (getContext() != null) {
            getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(LOCATION_KEY, locationName).apply();
        }
    }

    @Nullable
    private String readLocationFromCache() {
        Context context = getContext();
        String result = null;
        if (context != null) {
            result = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .getString(LOCATION_KEY, null);
        }
        return result != null ? result : null;
    }

    private String getMetaData(String key) {
        try {
            Context ctx = getContext();
            if (ctx == null) return null;
            Bundle metaData = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager
                    .GET_META_DATA).metaData;
            return metaData != null ? metaData.getString(key) : null;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }

    }

}
