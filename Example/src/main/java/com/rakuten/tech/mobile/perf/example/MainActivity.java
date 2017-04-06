package com.rakuten.tech.mobile.perf.example;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.rakuten.tech.mobile.perf.example.databinding.ActivityMainBinding;
import com.rakuten.tech.mobile.perf.runtime.Measurement;
import com.rakuten.tech.mobile.perf.runtime.Metric;
import com.rakuten.tech.mobile.perf.runtime.StandardMetric;

/**
 * MainActivity of the Example Application
 *
 */

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding activityMainBinding = null;
    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        final Handler handler = new Handler();

        activityMainBinding.testAggregatedMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String originalText = (String) activityMainBinding.testAggregatedMeasurement.getText();
                activityMainBinding.testAggregatedMeasurement.setText("RUNNING");

                final String imageUrl1 = "imageUrl1";
                final String imageUrl2 = "imageUrl2";
                Metric.start(StandardMetric.ITEM.getValue());
                Measurement.startAggregated("testAggregatedMeasurement",imageUrl1);
                Measurement.startAggregated("testAggregatedMeasurement",imageUrl2);

                new Thread(new Runnable() {
                    @Override
                    public void run () {
                        try {
                            Thread.sleep(1000);
                            Measurement.endAggregated("testAggregatedMeasurement",imageUrl1);
                            Measurement.endAggregated("testAggregatedMeasurement",imageUrl2);
                        } catch (InterruptedException e) {
                            Thread.interrupted();
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run () {
                                activityMainBinding.testAggregatedMeasurement.setText(originalText);
                                showDialog("Aggregated measurement for \"testAggregatedMeasurement\" done.");
                            }
                        });
                    }
                }).start();
            }
        });

        activityMainBinding.testMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String originalText = (String) activityMainBinding.testMeasurement.getText();
                activityMainBinding.testMeasurement.setText("RUNNING");
                Metric.start(StandardMetric.ITEM.getValue());
                Measurement.start("testMeasurement");
                new Thread(new Runnable() {
                    @Override
                    public void run () {
                        try {
                            Thread.sleep(1000);
                            Measurement.end("testMeasurement");
                        } catch (InterruptedException e) {
                            Thread.interrupted();
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run () {
                                activityMainBinding.testMeasurement.setText(originalText);
                                showDialog("Measurement for \"testMeasurement\" done.");
                            }
                        });
                    }
                }).start();

            }
        });

        activityMainBinding.testNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String originalText = (String) activityMainBinding.testNetwork.getText();
                activityMainBinding.testNetwork.setText("RUNNING");
                Metric.start(StandardMetric.ITEM.getValue());
                new Thread(new Runnable() {
                    @Override
                    public void run () {
                        try {
                            URL url = new URL("https://www.google.com/");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            try {
                                //InputStream input = conn.getInputStream();
                                BufferedInputStream input = new BufferedInputStream(conn.getInputStream());
                                byte[] buffer = new byte[1024];
                                while (input.read(buffer) > 0) {
                                }
                                input.close();
                            } finally {
                                conn.disconnect();
                            }

                            handler.post(new Runnable() {
                                @Override
                                public void run () {
                                    activityMainBinding.testNetwork.setText(originalText);
                                    showDialog("Network test for \"https://www.google.com/\" done.");
                                }
                            });
                        }
                        catch (Exception e) {
                            Log.d(TAG, "Network error");
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    activityMainBinding.testNetwork.setText(originalText);
                                    showDialog("No Network Connection.");
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }

    private void showDialog(String message) {
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag("dialog");
        if (prev != null)
            ft.remove(prev);
        ft.addToBackStack(null);
        DialogFragment newFragment = InfoDialog.newInstance(message);
        newFragment.show(fragmentManager, "dialog");
    }
}
