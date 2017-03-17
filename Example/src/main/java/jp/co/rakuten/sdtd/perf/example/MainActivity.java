package jp.co.rakuten.sdtd.perf.example;

import android.app.Activity;
import android.content.ComponentName;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import jp.co.rakuten.sdtd.perf.example.databinding.ActivityMainBinding;
import jp.co.rakuten.sdtd.perf.runtime.Measurement;
//import jp.co.rakuten.sdtd.perf.runtime.*;

public class MainActivity extends Activity {

    ActivityMainBinding activityMainBinding = null;
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
                final Comparable comparable = new Comparable() {
                    @Override
                    public int compareTo(Object o) {
                        return 0;
                    }
                };
                final Comparable comparable1 = new Comparable() {
                    @Override
                    public int compareTo(Object o) {
                        return 0;
                    }
                };
                Measurement.startAggregated("testAggregatedMeasurement",comparable);
                Measurement.startAggregated("testAggregatedMeasurement",comparable1);

                new Thread(new Runnable() {
                    @Override
                    public void run () {
                        try {
                            Thread.sleep(1000);
                            Measurement.endAggregated("testAggregatedMeasurement",comparable);
                            Measurement.endAggregated("testAggregatedMeasurement",comparable1);
                        } catch (InterruptedException e) {
                            Thread.interrupted();
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run () {
                                activityMainBinding.testAggregatedMeasurement.setText(originalText);
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
                                public void run () {activityMainBinding.testNetwork.setText(originalText);
                                }
                            });
                        }
                        catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
            }
        });
    }
}
