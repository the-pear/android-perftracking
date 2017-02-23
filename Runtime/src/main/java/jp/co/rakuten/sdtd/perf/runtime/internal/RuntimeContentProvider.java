package jp.co.rakuten.sdtd.perf.runtime.internal;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
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

import jp.co.rakuten.sdtd.perf.BuildConfig;
import jp.co.rakuten.sdtd.perf.core.Config;
import jp.co.rakuten.sdtd.perf.runtime.Measurement;
import jp.co.rakuten.sdtd.perf.runtime.StandardMetric;

/**
 * RuntimeContentProvider - a custom high-priority ContentProvider, to start tracking early in the process launch phase.
 *
 * @author RMSDK team(prj-rmsdk@mail.rakuten.com)
 */

public class RuntimeContentProvider extends ContentProvider {
    private static final String PREFS = "app_performance";
    private static final String CONFIG_KEY = "config_key";

    @Override
    public boolean onCreate() {
        if (getContext() == null) return false;
        // Load data from last configuration
        ConfigurationResult lastConfig = getLastConfiguration();
        Config config = null; // configuration for TrackingManager
        if (lastConfig != null) {
            double enablePercent = lastConfig.getEnablePercent();
            double randomNumber = new Random(System.currentTimeMillis()).nextInt(101);
            if (randomNumber <= enablePercent) {
                config = new Config();
                config.app = BuildConfig.APPLICATION_ID;
                config.version = BuildConfig.VERSION_NAME;
                config.debug = BuildConfig.DEBUG;
                config.eventHubUrl = lastConfig.getSendUrl();
                config.eventHubAuthorization = lastConfig.getHeader().getAuthorization();
            }
        }
        // Get latest configuration
        RequestQueue queue = new RequestQueue(new NoCache(), new BasicNetwork(new HurlStack()));
        queue.start();
        ConfigurationParam param = new ConfigurationParam.Builder()
                .setAppId(BuildConfig.APPLICATION_ID)
                .setAppVersion(BuildConfig.VERSION_NAME)
                .setCountryCode(getContext().getResources().getConfiguration().locale.getCountry())
                .setPlatform("android")
                .setSdkVersion(String.valueOf(Build.VERSION.SDK_INT))
                .build();
        new ConfigurationRequest(param, new Response.Listener<ConfigurationResult>() {
            @Override
            public void onResponse(ConfigurationResult response) {
                saveConfiguration(response); // save latest configuration
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(RuntimeContentProvider.class.getSimpleName(), error.getMessage());
            }
        }).queue(queue);
        if (config == null)
            return false; // TODO Config class should be a builder and have all the values set properly
        // Initialise Tracking Manager
        TrackingManager.initialize(getContext(), config);
        Measurement.start(StandardMetric.LAUNCH.getValue());
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
    private ConfigurationResult getLastConfiguration() {
        String result = null;
        if (getContext() != null)
            result = getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(CONFIG_KEY, null);
        if (result != null) return new Gson().fromJson(result, ConfigurationResult.class);
        else return null;
    }
}
