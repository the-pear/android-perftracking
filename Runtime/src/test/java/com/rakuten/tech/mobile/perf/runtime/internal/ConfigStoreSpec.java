package com.rakuten.tech.mobile.perf.runtime.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.rakuten.tech.mobile.perf.runtime.RobolectricUnitSpec;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(shadows = {
        RequestQueueShadow.class, // prevent network requests from runtime side
        TrackerShadow.class // prevent network requests from core side
})
public class ConfigStoreSpec extends RobolectricUnitSpec {

    @Rule public TestData config = new TestData("configuration-api-response.json");

    @Mock PackageManager packageManager;
    /* Spy */ private SharedPreferences prefs;
    /* Spy */ private Context context;
    /* Spy */private MockedQueue queue;

    private ConfigStore configStore;

    @SuppressLint("ApplySharedPref")
    @Before public void init() throws PackageManager.NameNotFoundException {
        RequestQueueShadow.queue = spy(new MockedQueue());
        queue = RequestQueueShadow.queue;
        context = spy(RuntimeEnvironment.application);
        when(context.getPackageManager()).thenReturn(packageManager);
        prefs = spy(context.getSharedPreferences("app_performance", Context.MODE_PRIVATE));
        prefs.edit().clear().apply();
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
        configStore = new ConfigStore(context,queue,"",null);

        queue.verify();
    }

    @SuppressLint("CommitPrefEdits")
    @Test public void shouldCacheConfigOnEmptyCache() throws JSONException {
        queue.rule().whenClass(ConfigurationRequest.class).returnNetworkResponse(200, config.content);

        configStore = new ConfigStore(context,queue,"",null);
        verify(prefs,times(1)).edit();
        String cachedResponse = prefs.getString("config_key", null);
        JSONAssert.assertEquals(config.content, cachedResponse, true);
    }

    @Test public void shouldNotFailOnFailedConfigRequest() {
        queue.rule().whenClass(ConfigurationRequest.class).returnError(new VolleyError(new Throwable()));

        configStore = new ConfigStore(context,queue,"",null);

        queue.verify();
    }

    @Test public void shouldDoWhatWhenSubscriptionKeyIsMissing() {
        configStore = new ConfigStore(context,queue,"",null);
    }
}
