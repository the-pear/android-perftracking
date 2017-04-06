package com.rakuten.tech.mobile.perf.core.mixins;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpResponse;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.HurlStack;

import com.rakuten.tech.mobile.perf.core.Tracker;
import com.rakuten.tech.mobile.perf.core.annotations.Exists;
import com.rakuten.tech.mobile.perf.core.annotations.MixClass;
import com.rakuten.tech.mobile.perf.core.annotations.ReplaceMethod;

@Exists(HurlStack.class)
@MixClass(HurlStack.class)
public class VolleyHurlStackMixin {

	@ReplaceMethod
    public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException, AuthFailureError {
		
		int id = Tracker.startMethod(request, "performRequest");
		
		try {
			return performRequest(request, additionalHeaders);
		}
		finally {
			Tracker.endMethod(id);
		}
    }
}
