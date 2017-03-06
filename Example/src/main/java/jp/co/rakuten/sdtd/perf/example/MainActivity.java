package jp.co.rakuten.sdtd.perf.example;

import android.app.Activity;
import android.os.Bundle;
//import jp.co.rakuten.sdtd.perf.runtime.*;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Measurement.start("200");
    }
}
