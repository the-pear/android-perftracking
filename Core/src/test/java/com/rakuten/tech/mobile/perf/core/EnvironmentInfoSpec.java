package com.rakuten.tech.mobile.perf.core;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Locale;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

public class EnvironmentInfoSpec {
    @Mock TelephonyManager tm;
    @Mock Context ctx;
    private CachingObservable<String> location = new CachingObservable<String>(null);
    private final String simCountry = "test-sim-country";
    private final String networkOperator = "test-network-operator";

    @Before public void initMocks() {
        MockitoAnnotations.initMocks(this);
        when(tm.getSimCountryIso()).thenReturn(simCountry);
        when(tm.getNetworkOperatorName()).thenReturn(networkOperator);
    }


    @Test public void shouldReadCountryAndNetworkFromTelephonyManager() {
        when(ctx.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(tm);
        EnvironmentInfo info = new EnvironmentInfo(ctx, location);
        assertThat(info).isNotNull();
        assertThat(info.country).isEqualTo(simCountry);
        assertThat(info.network).isEqualTo(networkOperator);
        assertThat(info.device).isEqualTo(Build.MODEL);
    }

    @Test
    public void shouldGetRegionFromLocationUpdate() {
        String region = "Hyderabad";
        EnvironmentInfo info = new EnvironmentInfo(ctx, location);

        location.publish(region);

        assertThat(info.getRegion()).isEqualTo(region);
    }

    @Test
    public void shouldPointToDefaultRegionWhenLocationIsNotUpdated() {
        EnvironmentInfo info = new EnvironmentInfo(ctx, location);

        assertThat(info.getRegion()).isEqualTo(null);
    }

    @Test
    public void shouldUseCachedLocationForInstanceCreation() {
        String region = "Bangalore";
        location.publish(region);

        EnvironmentInfo info = new EnvironmentInfo(ctx, location);

        assertThat(info.getRegion()).isEqualTo(region);
    }

    @Test
    public void shouldFallbackToReadCountryFromLocaleIfTelephonyManagerIsNull() {
        when(ctx.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(null);
        EnvironmentInfo info = new EnvironmentInfo(ctx, location);
        assertThat(info).isNotNull();
        assertThat(info.country).isEqualToIgnoringCase(Locale.getDefault().getCountry());
    }

    @SuppressWarnings("RedundantStringConstructorCall")
    @Test
    public void shouldFallbackToReadCountryFromLocaleIfCountryIsEmpty() {
        when(ctx.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(tm);
        // prevent string literal from being "intern"ed so `== ""` is false in test setting, see
        // http://stackoverflow.com/questions/27473457/in-java-why-does-string-string-evaluate-to-true-inside-a-method-as-opposed
        when(tm.getSimCountryIso()).thenReturn(new String(""));
        EnvironmentInfo info = new EnvironmentInfo(ctx, location);
        assertThat(info).isNotNull();
        assertThat(info.country).isEqualToIgnoringCase(Locale.getDefault().getCountry());
    }

    @Test
    public void shouldNormalizeCountryCodeToLowercase() {
        when(ctx.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(null);
        Locale.setDefault(new Locale("testLanguage", "Test-Locale-Country", "testVariant"));
        EnvironmentInfo info = new EnvironmentInfo(ctx, location);
        assertThat(info).isNotNull();
        assertThat(info.country).isEqualTo("test-locale-country");


        when(ctx.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(tm);
        when(tm.getSimCountryIso()).thenReturn("Test-Sim-Country");

        info = new EnvironmentInfo(ctx, location);
        assertThat(info).isNotNull();
        assertThat(info.country).isEqualTo("test-sim-country");
    }

}
