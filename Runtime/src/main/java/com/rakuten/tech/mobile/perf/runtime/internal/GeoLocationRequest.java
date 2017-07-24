package com.rakuten.tech.mobile.perf.runtime.internal;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import jp.co.rakuten.api.core.BaseRequest;

/**
 * GeoLocationRequest
 */

class GeoLocationRequest extends BaseRequest<GeoLocationResult> {
    private static final String DEFAULT_URL_PREFIX = "https://api.apps.global.rakuten.com/relay/location/v1";

    GeoLocationRequest(@Nullable String urlPrefix, String subscriptionKey, @Nullable Response.Listener<GeoLocationResult> listener, @Nullable Response.ErrorListener errorListener) {
        super(listener, errorListener);
        setMethod(Method.GET);
        setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
        String prefix = urlPrefix != null ? urlPrefix : DEFAULT_URL_PREFIX;
        Uri uri = Uri.parse(prefix);
        setUrl(uri.toString());
    }


    @Override
    protected GeoLocationResult parseResponse(String response) throws JsonSyntaxException, VolleyError {
        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        JsonArray jsonGeoLocationArray = json.getAsJsonObject().get("list").getAsJsonArray();
        JsonObject jsonGeoLocationObject = jsonGeoLocationArray.get(0).getAsJsonObject();

        JsonArray jsonSubdivisioinArray = jsonGeoLocationObject.getAsJsonObject().get("subdivisions").getAsJsonArray();
        JsonObject jsonFirstSubdivisioinObject = jsonSubdivisioinArray.get(0).getAsJsonObject();
        JsonObject jsonSubdivisioinNamesObject = jsonFirstSubdivisioinObject.get("names").getAsJsonObject();
        String jsonSubdivisioinNamesEnObject = jsonSubdivisioinNamesObject.get("en").getAsString();

        return new GeoLocationResult(jsonSubdivisioinNamesEnObject);
    }
}
