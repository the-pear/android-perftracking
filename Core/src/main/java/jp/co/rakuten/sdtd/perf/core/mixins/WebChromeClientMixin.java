package jp.co.rakuten.sdtd.perf.core.mixins;

import android.webkit.WebChromeClient;
import android.webkit.WebView;
import jp.co.rakuten.sdtd.perf.core.Tracker;
import jp.co.rakuten.sdtd.perf.core.annotations.ChangeBaseTo;
import jp.co.rakuten.sdtd.perf.core.annotations.MixSubclassOf;
import jp.co.rakuten.sdtd.perf.core.annotations.ReplaceMethod;
import jp.co.rakuten.sdtd.perf.core.base.WebChromeClientBase;

@MixSubclassOf(WebChromeClient.class)
@ChangeBaseTo(WebChromeClientBase.class)
public class WebChromeClientMixin extends WebChromeClientBase {

    @ReplaceMethod
    public void onProgressChanged (WebView view, int newProgress) {
        Tracker.prolongMetric();
        onProgressChanged(view, newProgress);
    }
}
