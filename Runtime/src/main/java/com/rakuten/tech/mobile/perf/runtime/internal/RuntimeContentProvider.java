package com.rakuten.tech.mobile.perf.runtime.internal;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;
import com.google.gson.Gson;

import java.util.Random;

import com.rakuten.tech.mobile.perf.core.Config;
import com.rakuten.tech.mobile.perf.runtime.Metric;
import com.rakuten.tech.mobile.perf.runtime.StandardMetric;

/**
 * RuntimeContentProvider - a custom high-priority ContentProvider, to start tracking early in the process launch phase.
 *
 */

public class RuntimeContentProvider extends ContentProvider {
    private static final String TAG = RuntimeContentProvider.class.getSimpleName();
    private static final String PREFS = "app_performance";
    private static final String CONFIG_KEY = "config_key";

    @Override
    public boolean onCreate() {
        Context context = getContext();
        if (context == null) return false;
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        // Load data from last configuration
        ConfigurationResult lastConfig = getLastConfiguration(context);
        Config config = null; // configuration for TrackingManager
        if (lastConfig != null) {
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
                } catch (PackageManager.NameNotFoundException e) {
                    Log.d(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
                    config.debug = false;
                } catch (NullPointerException e) {
                    Log.d(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
                    config.debug = false;
                }
                config.eventHubUrl = lastConfig.getSendUrl();
                config.header = lastConfig.getHeader();
            }
        }
        // Get latest configuration
        RequestQueue queue = new RequestQueue(new NoCache(), new BasicNetwork(new HurlStack()));
        queue.start();
        ConfigurationParam param = null;
        try {
            param = new ConfigurationParam.Builder()
                    .setAppId(packageName)
                    .setAppVersion(packageManager
                            .getPackageInfo(packageName, 0).versionName)
                    .setCountryCode(context.getResources().getConfiguration().locale.getCountry())
                    .setPlatform("android")
                    .setSdkVersion(String.valueOf(Build.VERSION.SDK_INT))
                    .build();
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, e.getMessage());
        }
        if (param != null) {
            new ConfigurationRequest(param, new Response.Listener<ConfigurationResult>() {
                @Override
                public void onResponse(ConfigurationResult response) {
                    saveConfiguration(response); // save latest configuration
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
            }).queue(queue);
        }
        if (config != null) {
            // Initialise Tracking Manager
            TrackingManager.initialize(getContext(), config); // TODO Config class should be a builder and have all the values set properly
            Metric.start(StandardMetric.LAUNCH.getValue());
        }
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private void saveConfiguration(ConfigurationResult result) {
        if (getContext() != null)
            getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(CONFIG_KEY, new Gson().toJson(result)).apply();
    }

    @Nullable
    private ConfigurationResult getLastConfiguration(Context context) {
        String result = null;
        if (context != null)
            result = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(CONFIG_KEY, null);
        if (result != null) return new Gson().fromJson(result, ConfigurationResult.class);
        else return null;
    }
}
