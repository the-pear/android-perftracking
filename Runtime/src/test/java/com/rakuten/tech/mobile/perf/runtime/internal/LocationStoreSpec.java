package com.rakuten.tech.mobile.perf.runtime.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import com.android.volley.VolleyError;
import com.rakuten.tech.mobile.perf.runtime.RobolectricUnitSpec;
import com.rakuten.tech.mobile.perf.runtime.TestData;
import com.rakuten.tech.mobile.perf.runtime.shadow.RequestQueueShadow;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import jp.co.rakuten.api.test.MockedQueue;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@Config(shadows = {
        RequestQueueShadow.class, // prevent network requests from runtime side
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
        when(context.getPackageManager()).thenReturn(packageManager);
        prefs = spy(context.getSharedPreferences("app_performance", Context.MODE_PRIVATE));
        prefs.edit().clear().apply();
        clearInvocations(prefs);
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(prefs);
    }

    @Test public void shouldRequestLocationOnEmptyCache() throws JSONException {
        queue.rule().whenClass(GeoLocationRequest.class).returnNetworkResponse(200, location.content);

        locationStore = new LocationStore(context, queue, "", null);

        queue.verify();
    }

    @Test public void shouldCacheLocationOnEmptyCache() throws JSONException {
        String expectedValue = "Tokyo";
        queue.rule().whenClass(GeoLocationRequest.class).returnNetworkResponse(200, location.content);

        locationStore = new LocationStore(context, queue, "", null);

        String storeValue = locationStore.getObservable().getCachedValue();
        assertThat(storeValue).isEqualTo(expectedValue);
    }

    @Test public void shouldUseCachedLocationForInstanceCreation() throws JSONException {
        String prefsValue = "Tokyo";
        prefs.edit().putString("location_key", prefsValue).apply();

        locationStore = new LocationStore(context, queue, "", null);

        String storeValue = locationStore.getObservable().getCachedValue();
        assertThat(storeValue).isEqualTo(prefsValue);
    }

    @Test public void shouldUseNullLocationOnEmptyCacheForInstanceCreation() throws JSONException {
        locationStore = new LocationStore(context, queue, "", null);

        String storeValue = locationStore.getObservable().getCachedValue();
        assertThat(storeValue).isEqualTo(null);
    }

    @Test public void shouldNotFailOnFailedLocationRequest() {
        queue.rule().whenClass(GeoLocationRequest.class).returnError(new VolleyError(new Throwable()));

        locationStore = new LocationStore(context, queue, "", null);

        queue.verify();
    }

    @Test public void shouldDoWhatWhenSubscriptionKeyIsMissing() {
        locationStore = new LocationStore(context, queue, null, null);
    }
}