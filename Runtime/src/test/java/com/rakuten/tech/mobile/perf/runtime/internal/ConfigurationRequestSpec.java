package com.rakuten.tech.mobile.perf.runtime.internal;

import com.android.volley.Request;
import com.rakuten.tech.mobile.perf.runtime.RobolectricUnitSpec;
import com.rakuten.tech.mobile.perf.runtime.TestData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import static com.rakuten.tech.mobile.perf.runtime.TestCondition.keyValue;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class ConfigurationRequestSpec extends RobolectricUnitSpec {
    @Mock ConfigurationRequest testMock;
    private ConfigurationParam.Builder builder;

    @Before public void initConfig() {
        builder = new ConfigurationParam.Builder()
                .setAppId("testId")
                .setAppVersion("testVersion")
                .setSdkVersion("testSdkVersion")
                .setCountryCode("testCountryCode")
                .setPlatform("testPlatform");
    }

    @Test public void shouldConstructWithNullableParameters() {
        ConfigurationRequest request = new ConfigurationRequest(null, "", builder.build(), null, null);
        assertThat(request).isNotNull();
        assertThat(request.getMethod()).isEqualTo(Request.Method.GET);
    }

    @Test public void shouldBuildUrlWithDefaultUrlPrefix() {
        ConfigurationRequest request = new ConfigurationRequest(null, "", builder.build(), null, null);
        assertThat(request.getUrl())
                .isEqualTo("https://perf-config-api-dev-japaneast.azurewebsites.net/api/v1/platform/testPlatform/app/testId/version/testVersion/?sdk=testSdkVersion&country=testCountryCode");
    }

    @Test public void shouldBuildUrlWithCustomDomain() {
        ConfigurationRequest request = new ConfigurationRequest("https://rakuten.co.jp", "", builder
                .build(), null, null);
        assertThat(request.getUrl())
                .isEqualTo("https://rakuten.co.jp/platform/testPlatform/app/testId/version/testVersion/?sdk=testSdkVersion&country=testCountryCode");
    }

    @Test public void shouldBuildUrlWithCustomPrefix() {
        ConfigurationRequest request =
                new ConfigurationRequest("https://api.apps.global.rakuten.com/stg/performance/config/v1",
                        "", builder.build(), null, null);
        assertThat(request.getUrl())
                .isEqualTo("https://api.apps.global.rakuten.com/stg/performance/config/v1/platform/testPlatform/app/testId/version/testVersion/?sdk=testSdkVersion&country=testCountryCode");
    }

    @Test public void shouldSetSubscriptionKeyHeader() {
        String testKey = "testKey";
        ConfigurationRequest request = new ConfigurationRequest(null, testKey, builder.build(),
                null, null);
        assertThat(request.getHeaders()).has(keyValue("Ocp-Apim-Subscription-Key", testKey));
    }

    @Rule public TestData data = new TestData("configuration-api-response.json");
    @Test public void shouldParseResponse() {
        ConfigurationRequest request = new ConfigurationRequest(null, "", builder.build(), null, null);
        ConfigurationResult response = request.parseResponse(data.content);
        assertThat(response).isNotNull();
        assertThat(response.getSendUrl()).isEqualTo("https://perf-eventhub-dev-japaneast.servicebus.windows.net/measurements/messages?timeout=5&api-version=2014-01");
        assertThat(response.getEnablePercent()).isEqualTo(100.0);
        assertThat(response.getHeader()).isNotNull();
        assertThat(response.getHeader()).has(keyValue("Authorization", "SharedAccessSignature sr=perf-eventhub-dev-japaneast&sig=YN%2FKvrSzkwoKELcJnb%2Fdy414mRM3F1s9HIteg9gf9f8%3D&se=1519954045&skn=SendOnly"));
        assertThat(response.getHeader()).has(keyValue("BrokerProperties", "{\"PartitionKey\": \"com.rakuten.tech.mobile.perf.example/1.0.0\"}"));
        assertThat(response.getHeader()).has(keyValue("Content-Type", "application/atom+xml;type=entry;charset=utf-8"));
    }
}
