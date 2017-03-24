package jp.co.rakuten.sdtd.perf.core.base;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import jp.co.rakuten.sdtd.perf.core.Tracker;

public class WebViewClientBase extends WebViewClient {
    public int jp_co_rakuten_sdtd_perf_page_trackingId;

    public void onPageStarted (WebView view, String url, Bitmap favicon) {
        Tracker.prolongMetric();
        if (jp_co_rakuten_sdtd_perf_page_trackingId == 0) {
            jp_co_rakuten_sdtd_perf_page_trackingId = Tracker.startUrl(url, "VIEW");
        }
        super.onPageStarted(view, url, favicon);
    }

    public void onPageFinished (WebView view, String url) {
        Tracker.prolongMetric();
        if (jp_co_rakuten_sdtd_perf_page_trackingId != 0) {
            Tracker.endUrl(jp_co_rakuten_sdtd_perf_page_trackingId);
            jp_co_rakuten_sdtd_perf_page_trackingId = 0;
        }
        super.onPageFinished(view, url);
    }
}
