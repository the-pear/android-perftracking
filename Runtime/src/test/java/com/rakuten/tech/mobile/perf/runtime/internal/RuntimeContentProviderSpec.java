package com.rakuten.tech.mobile.perf.runtime.internal;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.rakuten.tech.mobile.perf.runtime.RobolectricUnitSpec;
import com.rakuten.tech.mobile.perf.runtime.StandardMetric;
import com.rakuten.tech.mobile.perf.runtime.TestData;
import com.rakuten.tech.mobile.perf.runtime.shadow.RequestQueueShadow;
import com.rakuten.tech.mobile.perf.runtime.shadow.TrackerShadow;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import jp.co.rakuten.api.test.MockedQueue;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(shadows = {
        RequestQueueShadow.class, // prevent network requests from runtime side
        TrackerShadow.class, // prevent network requests from core side
        StoreShadow.class // fake cache
})
public class RuntimeContentProviderSpec extends RobolectricUnitSpec {

    @Rule public TestData config = new TestData("configuration-api-response.json");

    @Mock PackageManager packageManager;
    /* Spy */private MockedQueue queue;

    private RuntimeContentProvider provider;

    @Before public void init() throws PackageManager.NameNotFoundException {
        RequestQueueShadow.queue = spy(new MockedQueue());
        queue = RequestQueueShadow.queue;
        provider = spy(new RuntimeContentProvider());
        Context context = spy(RuntimeEnvironment.application);
        when(provider.getContext()).thenReturn(context);
        when(context.getPackageManager()).thenReturn(packageManager);
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

    @Test public void shouldNotStartTrackingOnEmptyCache() {
        StoreShadow.cachedContent = null;

        provider.onCreate();

        assertThat(TrackingManager.INSTANCE).isNull();
        verify(TrackerShadow.mockTracker, never()).startMetric(anyString());
    }

    @Test public void shouldStartTrackingAndLaunchMetricOnCachedConfig() {
        StoreShadow.cachedContent = new Gson().fromJson(config.content, ConfigurationResult.class);

        provider.onCreate();

        assertThat(TrackingManager.INSTANCE).isNotNull();
        verify(TrackerShadow.mockTracker).startMetric(StandardMetric.LAUNCH.getValue());
    }

    @Test public void shouldNotFailOnMissingPackageInfo() throws PackageManager.NameNotFoundException {
        doThrow(new PackageManager.NameNotFoundException())
                .when(packageManager).getPackageInfo(anyString(), anyInt());

        provider.onCreate();

        verify(queue).add(any(Request.class));
    }

    @Test public void shouldStartTrackingEvenWhenPackageAndAppInfoIsMissing() throws PackageManager.NameNotFoundException {
        StoreShadow.cachedContent = new Gson().fromJson(config.content, ConfigurationResult.class);
        doThrow(new PackageManager.NameNotFoundException())
                .when(packageManager).getPackageInfo(anyString(), anyInt());
        doThrow(new PackageManager.NameNotFoundException())
                .when(packageManager).getApplicationInfo(anyString(), anyInt());

        provider.onCreate();

        assertThat(TrackingManager.INSTANCE).isNotNull();
        verify(TrackerShadow.mockTracker).startMetric(StandardMetric.LAUNCH.getValue());
    }


    @Test public void shouldNotImplementAnyContentProviderMethods() {
        assertThat(provider.query(null, null, null, null, null)).isNull();
        assertThat(provider.getType(null)).isNull();
        assertThat(provider.insert(null, null)).isNull();
        assertThat(provider.delete(null, null, null)).isEqualTo(0);
        assertThat(provider.update(null, null, null, null)).isEqualTo(0);
    }
}
