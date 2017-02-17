package jp.co.rakuten.sdtd.perf.core.detours;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import jp.co.rakuten.sdtd.perf.core.annotations.DetourCall;
import jp.co.rakuten.sdtd.perf.core.wrappers.HttpURLConnectionWrapper;
import jp.co.rakuten.sdtd.perf.core.wrappers.HttpsURLConnectionWrapper;

public class URLDetours {

    @DetourCall
    public static URLConnection openConnection(URL url) throws IOException {
        URLConnection conn = url.openConnection();
        if (conn instanceof HttpsURLConnection) {
            return new HttpsURLConnectionWrapper((HttpsURLConnection)conn);
        }
        if (conn instanceof HttpURLConnection) {
            return new HttpURLConnectionWrapper((HttpURLConnection)conn);
        }
        return conn;
    }
}
