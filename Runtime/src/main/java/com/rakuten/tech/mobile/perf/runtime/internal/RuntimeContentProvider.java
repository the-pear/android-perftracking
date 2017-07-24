package com.rakuten.tech.mobile.perf.runtime.internal;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;
import com.rakuten.tech.mobile.perf.core.Config;
import com.rakuten.tech.mobile.perf.runtime.Metric;
import com.rakuten.tech.mobile.perf.runtime.StandardMetric;

import java.util.Random;


/**
 * RuntimeContentProvider - a custom high-priority ContentProvider, to start tracking early in the process launch phase.
 */

public class RuntimeContentProvider extends ContentProvider {
    private static final String TAG = RuntimeContentProvider.class.getSimpleName();

    private Context mContext;
    private RequestQueue mQueue;

    @Override
    public boolean onCreate() {
        mContext = getContext();
        if (mContext == null) return false;
        if (!AppPerformanceConfig.enabled) return false; // Return when instrumentation is disabled

        mQueue = new RequestQueue(new NoCache(), new BasicNetwork(new HurlStack()));
        mQueue.start();

        String subscriptionkey = getMetaData("com.rakuten.tech.mobile.perf.SubscriptionKey");
        String urlPrefix = getMetaData("com.rakuten.tech.mobile.perf.ConfigurationUrlPrefix");
        ConfigStore configStore = new ConfigStore(mContext, mQueue, subscriptionkey, urlPrefix);
        LocationStore locationStore = new LocationStore(mContext, mQueue, subscriptionkey, urlPrefix);

        ConfigurationResult lastConfig = configStore.readConfigFromCache();
        Config config = createConfig(mContext, lastConfig);
        if (config != null) {
            // Initialise Tracking Manager
            TrackingManager.initialize(mContext, config, locationStore.getObservable());
            Metric.start(StandardMetric.LAUNCH.getValue());
        }
        return false;
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
