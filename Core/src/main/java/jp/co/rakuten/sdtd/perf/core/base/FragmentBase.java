package jp.co.rakuten.sdtd.perf.core.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import jp.co.rakuten.sdtd.perf.core.Tracker;

public class FragmentBase extends Fragment {

    public boolean jp_co_rakuten_sdtd_perf_onCreate_tracking = false;

    public void onAttach (Activity activity) {
        Tracker.prolongMetric();
        super.onAttach(activity);
    }

    public void onCreate (Bundle savedInstanceState) {
        Tracker.prolongMetric();
        super.onCreate(savedInstanceState);
    }

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Tracker.prolongMetric();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void onActivityCreated (Bundle savedInstanceState) {
        Tracker.prolongMetric();
        super.onActivityCreated(savedInstanceState);
    }

    public void onViewStateRestored (Bundle savedInstanceState) {
        Tracker.prolongMetric();
        super.onViewStateRestored(savedInstanceState);
    }

    public void onStart () {
        Tracker.prolongMetric();
        super.onStart();
    }

    public void onResume () {
        Tracker.prolongMetric();
        super.onResume();
    }

    public void onPause () {
        Tracker.prolongMetric();
        super.onPause();
    }

    public void onStop () {
        Tracker.prolongMetric();
        super.onStop();
    }

    public void onDestroyView () {
        Tracker.prolongMetric();
        super.onDestroyView();
    }

    public void onDestroy () {
        Tracker.prolongMetric();
        super.onDestroy();
    }

    public void onDetach () {
        Tracker.prolongMetric();
        super.onDetach();
    }

    public void onHiddenChanged (boolean hidden) {
        Tracker.prolongMetric();
        super.onHiddenChanged(hidden);
    }

    public void onInflate (Context context, AttributeSet attrs, Bundle savedInstanceState) {
        Tracker.prolongMetric();
        super.onInflate(context, attrs, savedInstanceState);
    }
}

