package com.rakuten.tech.mobile.perf.runtime.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.rakuten.tech.mobile.perf.runtime.RobolectricUnitSpec;
import com.rakuten.tech.mobile.perf.runtime.StandardMetric;
import com.rakuten.tech.mobile.perf.runtime.TestData;
import com.rakuten.tech.mobile.perf.runtime.shadow.RequestQueueShadow;
import com.rakuten.tech.mobile.perf.runtime.shadow.TrackerShadow;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.skyscreamer.jsonassert.JSONAssert;

import jp.co.rakuten.api.test.MockedQueue;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(shadows = {
        RequestQueueShadow.class, // prevent network requests from runtime side
        TrackerShadow.class // prevent network requests from core side
})
public class RuntimeContentProviderSpec extends RobolectricUnitSpec {

    @Rule public TestData config = new TestData("configuration-api-response.json");

    @Mock PackageManager packageManager;
    /* Spy */ private SharedPreferences prefs;
    /* Spy */ private Context context;
    /* Spy */private MockedQueue queue;

    private RuntimeContentProvider provider;

    @SuppressLint("ApplySharedPref")
    @Before public void init() throws PackageManager.NameNotFoundException {
        RequestQueueShadow.queue = spy(new MockedQueue());
        queue = RequestQueueShadow.queue;
        provider = spy(new RuntimeContentProvider());
        context = spy(RuntimeEnvironment.application);
        when(provider.getContext()).thenReturn(context);
        when(context.getPackageManager()).thenReturn(packageManager);
        prefs = spy(context.getSharedPreferences("app_performance", Context.MODE_PRIVATE));
        prefs.edit().clear().commit();
        clearInvocations(prefs);
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(prefs);
        PackageInfo pkgInfo = new PackageInfo();
        pkgInfo.versionName = "testVersion";
        when(packageManager.getPackageInfo(anyString(), anyInt())).thenReturn(pkgInfo);
        ApplicationInfo appInfo = new ApplicationInfo();
        appInfo.metaData = new Bundle();
        when(packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA))
                .thenReturn(appInfo);
        TrackingManager.INSTANCE = null;
        clearInvocations(TrackerShadow.mockTracker);
    }

    @SuppressLint("CommitPrefEdits")
    @Test public void shouldRequestConfigOnEmptyCache() throws JSONException {
        queue.rule().whenClass(ConfigurationRequest.class).returnNetworkResponse(200, config.content);

        provider.onCreate();

        queue.verify();
    }

    @SuppressLint("CommitPrefEdits")
    @Test public void shouldCacheConfigOnEmptyCache() throws JSONException {
        queue.rule().whenClass(ConfigurationRequest.class).returnNetworkResponse(200, config.content);

        provider.onCreate();
        // Once for config cache and another for location cache
        verify(prefs,times(1)).edit();
        String cachedResponse = prefs.getString("config_key", null);
        JSONAssert.assertEquals(config.content, cachedResponse, true);
    }

    @Test public void shouldNotFailOnFailedConfigRequest() {
        queue.rule().whenClass(ConfigurationRequest.class).returnError(new VolleyError(new Throwable()));

        provider.onCreate();

        queue.verify();
        // no exception
    }

    @Test public void shouldNotStartTrackingOnEmptyCache() {
        provider.onCreate();

        assertThat(TrackingManager.INSTANCE).isNull();
        verify(TrackerShadow.mockTracker, never()).startMetric(anyString());
    }


    @SuppressLint("ApplySharedPref")
    @Test public void shouldStartTrackingAndLaunchMetricOnCachedConfig() {
        queue.rule().whenClass(ConfigurationRequest.class).returnNetworkResponse(200, config.content);
        prefs.edit().putString("config_key", config.content).commit();

        provider.onCreate();

        assertThat(TrackingManager.INSTANCE).isNotNull();
        verify(TrackerShadow.mockTracker, times(1)).startMetric(StandardMetric.LAUNCH.getValue());
    }

    @Test public void shouldNotFailOnMissingPackageInfo() throws PackageManager.NameNotFoundException {
        doThrow(new PackageManager.NameNotFoundException())
                .when(packageManager).getPackageInfo(anyString(), anyInt());

        provider.onCreate();

        // no exception
        // 0 request to Config API
        // 1 request to Location API
        verify(queue, times(1)).add(any(Request.class));
    }

    @SuppressLint("ApplySharedPref")
    @Test public void shouldStartTrackingEvenWhenPackageAndAppInfoIsMissing() throws PackageManager.NameNotFoundException {
        prefs.edit().putString("config_key", config.content).commit();
        doThrow(new PackageManager.NameNotFoundException())
                .when(packageManager).getPackageInfo(anyString(), anyInt());
        doThrow(new PackageManager.NameNotFoundException())
                .when(packageManager).getApplicationInfo(anyString(), anyInt());

        provider.onCreate();

        assertThat(TrackingManager.INSTANCE).isNotNull();
        verify(TrackerShadow.mockTracker, times(1)).startMetric(StandardMetric.LAUNCH.getValue());
    }

    @Test public void shouldDoWhatWhenSubscriptionKeyIsMissing() {
        provider.onCreate();
    }

    @Test public void shouldNotImplementAnyContentProviderMethods() {
        assertThat(provider.query(null, null, null, null, null)).isNull();
        assertThat(provider.getType(null)).isNull();
        assertThat(provider.insert(null, null)).isNull();
        assertThat(provider.delete(null, null, null)).isEqualTo(0);
        assertThat(provider.update(null, null, null, null)).isEqualTo(0);

    }
}
