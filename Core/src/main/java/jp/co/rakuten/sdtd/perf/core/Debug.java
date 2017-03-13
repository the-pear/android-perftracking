package jp.co.rakuten.sdtd.perf.core;

import android.util.Log;

public class Debug {
    private final String TAG = "PERF";

    public void log(String msg) {
        Log.d(TAG, msg);
    }
}
