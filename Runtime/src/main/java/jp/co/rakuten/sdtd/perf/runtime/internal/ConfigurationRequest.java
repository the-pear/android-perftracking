package jp.co.rakuten.sdtd.perf.runtime.internal;

import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Response;
import com.google.gson.Gson;

import jp.co.rakuten.api.core.BaseRequest;

/**
 * ConfigurationRequest
 */

class ConfigurationRequest extends BaseRequest<ConfigurationResult> {
    private static final String DOMAIN_URL_DEV = "http://perf-config-api-dev-japaneast.azurewebsites.net";
    private static final String PATH_API_VERSION = "/api/v1";

    ConfigurationRequest(ConfigurationParam param, @Nullable Response.Listener<ConfigurationResult> listener, @Nullable Response.ErrorListener errorListener) {
        super(listener, errorListener);
        setMethod(Method.GET);
        setHeader("Ocp-Apim-Subscription-Key", "subscriptionKey");// TODO Inject subscriptionKey from gradle
        setDomain(DOMAIN_URL_DEV); // TODO Have domain for prod and staging as well and inject from gradle
        setUrlPath(PATH_API_VERSION + "platform" + param.getPlatform() + "app" + param.getAppId() + "version" + param.getAppVersion());
        setQueryParam("sdk", param.getSdkVersion());
        setQueryParam("country", param.getCountryCode());
    }

    @Override
    @Nullable
    protected ConfigurationResult parseResponse(String response) throws Exception {
        ConfigurationResult result = null;
        try {
            result = new Gson().fromJson(response, ConfigurationResult.class);
        } catch (Exception e) {
            Log.e(ConfigurationRequest.class.getSimpleName(), e.getMessage());
        }
        return result;
    }
}
