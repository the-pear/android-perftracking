package jp.co.rakuten.sdtd.perf.runtime.internal;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import jp.co.rakuten.sdtd.perf.runtime.Measurement;
import jp.co.rakuten.sdtd.perf.runtime.StandardMetric;

/**
 * RuntimeContentProvider - a custom high-priority ContentProvider, to start tracking early in the process launch phase.
 *
 * @author RMSDK team(prj-rmsdk@mail.rakuten.com)
 */

public class RuntimeContentProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        // TODO Check Shared Preference flag for enabling Tracking
        Measurement.start(StandardMetric.LAUNCH.getValue());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
