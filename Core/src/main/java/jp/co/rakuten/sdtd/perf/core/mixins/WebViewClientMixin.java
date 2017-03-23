package jp.co.rakuten.sdtd.perf.core.mixins;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import jp.co.rakuten.sdtd.perf.core.Tracker;
import jp.co.rakuten.sdtd.perf.core.annotations.ChangeBaseTo;
import jp.co.rakuten.sdtd.perf.core.annotations.MixSubclassOf;
import jp.co.rakuten.sdtd.perf.core.annotations.ReplaceMethod;
import jp.co.rakuten.sdtd.perf.core.base.WebViewClientBase;

@MixSubclassOf(WebViewClient.class)
@ChangeBaseTo(WebViewClientBase.class)
public class WebViewClientMixin extends WebViewClientBase {

    @ReplaceMethod
    public void onPageStarted (WebView view, String url, Bitmap favicon) {
        Tracker.prolongMetric();
        jp_co_rakuten_sdtd_perf_page_trackingId = Tracker.startUrl(url, "VIEW");
        onPageStarted(view, url, favicon);
    }

    @ReplaceMethod
    public void onPageFinished (WebView view, String url) {
        Tracker.prolongMetric();
        Tracker.endUrl(jp_co_rakuten_sdtd_perf_page_trackingId);
        jp_co_rakuten_sdtd_perf_page_trackingId = 0;
        onPageFinished(view, url);
    }
}

