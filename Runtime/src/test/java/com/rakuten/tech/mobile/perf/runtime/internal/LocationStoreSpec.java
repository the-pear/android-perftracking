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
import com.rakuten.tech.mobile.perf.runtime.TestData;
import com.rakuten.tech.mobile.perf.runtime.shadow.RequestQueueShadow;
import com.rakuten.tech.mobile.perf.runtime.shadow.TrackerShadow;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import jp.co.rakuten.api.test.MockedQueue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(shadows = {
        RequestQueueShadow.class, // prevent network requests from runtime side
        TrackerShadow.class // prevent network requests from core side
})
public class LocationStoreSpec extends RobolectricUnitSpec {

    @Rule public TestData location = new TestData("geolocation-api-response.json");

    @Mock PackageManager packageManager;
    /* Spy */ private SharedPreferences prefs;
    /* Spy */ private Context context;
    /* Spy */private MockedQueue queue;

    private LocationStore locationStore;

    @SuppressLint("ApplySharedPref")
    @Before public void init() throws PackageManager.NameNotFoundException {
        RequestQueueShadow.queue = spy(new MockedQueue());
        queue = RequestQueueShadow.queue;
        context = spy(RuntimeEnvironment.application);
        //locationStore = spy(new LocationStore(context,queue,"",null));
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
    @Test public void shouldRequestLocationOnEmptyCache() throws JSONException {
        queue.rule().whenClass(GeoLocationRequest.class).returnNetworkResponse(200, location.content);
        locationStore = new LocationStore(context,queue,"",null);
        queue.verify();
    }

    @SuppressLint("CommitPrefEdits")
    @Test public void shouldCacheLocationOnEmptyCache() throws JSONException {
        queue.rule().whenClass(GeoLocationRequest.class).returnNetworkResponse(200, location.content);
        locationStore = new LocationStore(context,queue,"",null);
        verify(prefs,times(1)).edit();

        String cachedLocationResponse = prefs.getString("location_key", null);
        Assert.assertEquals("Tokyo",cachedLocationResponse);
    }

    @Test public void shouldNotFailOnFailedLocationRequest() {
        queue.rule().whenClass(GeoLocationRequest.class).returnError(new VolleyError(new Throwable()));
        locationStore = new LocationStore(context,queue,"",null);
        queue.verify();
    }


    @Test public void shouldNotFailOnMissingPackageInfo() throws PackageManager.NameNotFoundException {
        doThrow(new PackageManager.NameNotFoundException())
                .when(packageManager).getPackageInfo(anyString(), anyInt());
        locationStore = new LocationStore(context,queue,"",null);
        // no exception
        verify(queue, times(1)).add(any(Request.class));
    }

}
