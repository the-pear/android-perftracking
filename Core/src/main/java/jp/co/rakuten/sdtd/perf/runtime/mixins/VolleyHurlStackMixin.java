package jp.co.rakuten.sdtd.perf.runtime.mixins;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpResponse;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.HurlStack;

import jp.co.rakuten.sdtd.perf.runtime.Tracker;
import jp.co.rakuten.sdtd.perf.runtime.annotations.MixClass;
import jp.co.rakuten.sdtd.perf.runtime.annotations.ReplaceMethod;

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
