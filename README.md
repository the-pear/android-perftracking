# Performance Tracking

The Performance Tracking module enables applications to measure the time taken for executing user scenarios like searching products and displaying product details.
Each scenario/metric lists various method, network call measurements. Performance Tracking even provides api's for starting custom measurements.

## Installation procedure

### REMS Performance Tracking Credentials

Your app must be registered in the Relay Portal to use the App Performance Tracking feature.
Request for an App Performance Tracking Subscription Key through the [Inquiry Form](https://confluence.rakuten-it.com/confluence/display/REMI/REM+Inquiry+form) with your application Bundle id (iOS)/Package name (Android)

If you have any questions, please visit our [Documentation Portal] (https://developers.rakuten.com/hc/en-us/categories/115000711608-Rakuten-Ecosystem-Mobile-REM-) or you may contact us through the [Inquiry Form](https://confluence.rakuten-it.com/confluence/display/REMI/REM+Inquiry+form)

### Before you begin

+ Go through [Android Integration Checklist](http://www.raksdtd.com/android/android-integration-checklist/) to understand how to integrate the SDK properly.
### #1 Add dependency to buildscript


```groovy
apply plugin: 'com.rakuten.tech.mobile.perf'

   buildscript {
       repositories {
           maven { url 'http://artifactory.raksdtd.com/artifactory/libs-release' }
       }
       dependencies {
           classpath 'com.rakuten.tech.mobile.perf:plugin:0.1.0'
       }
   }
```


### #2 Provide Subscription key

You must provide Configuration api's subscription key as metadata in application manifest file.

```xml
<manifest>
    <application>
        <meta-data android:name="com.rakuten.tech.mobile.perf.SubscriptionKey"
            android:value="subscriptionKey" />
    </application>
</manifest>
```

### #3 Measurements

#### Metrics
Metrics are defined to measure how long something takes from the user perspective.
Standard metrics include launch and user interactions like searching products and displaying product details.
One metric measurement typically involves many low level measurements like method and network calls.
Performance Tracking SDK will automatically start the launch metrics(_launch) once you integrate into your app.
There can be only one metric running at any given point of time.
The Performance Tracking SDK features automatic metric termination.
That means developers are only required to start metrics and the SDK takes care of the rest by following a rule set.

**What makes a metric start :**<br />
    -The launch metric is started automatically by the SDK at the launch of the application<br />
    -Other metrics are started manually through the SDK's public API<br />

**What makes a metric keep going :**<br />
    -Activity life cycle changes<br />
    -Fragment life cycle and visibility changes<br />
    -Loading a page in WebView<br />

**What makes a metric terminate :**<br />
    -User interactions (clicks, back button pushed, etc.)<br />
    -WebView finishes loading a page<br />
    -Timeout of 10 seconds<br />
    -New metric start<br />

User can start new metric as shown in below sample.

```java
        Metric.start(StandardMetric.ITEM.getValue());
```

#### Custom measurements
Custom measurements give developers the ability to run arbitrary measurements, for which they provide the start and end.
Using the public api's of the runtime SDK , you can start new custom measurements as shown below.

```java

import com.rakuten.tech.mobile.perf.runtime.Measurement;
import com.rakuten.tech.mobile.perf.runtime.Metric;
import com.rakuten.tech.mobile.perf.runtime.StandardMetric;

        Metric.start(StandardMetric.ITEM.getValue());
        Measurement.start("demoTestMeasurement");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    Measurement.end("demoTestMeasurement");
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
        }).start();
```

#### Aggregated measurements
Aggregated measurements are useful for scenarios where multiple measurements of the same type run in parallel.
Aggregated measurements take an extra parameter which is used to distinguish between multiple running measurements.

```java
        Metric.start(StandardMetric.ITEM.getValue());
        Measurement.startAggregated("demoTestAggregated", "FirstImageUrl");
        Measurement.startAggregated("demoTestAggregated", "SecondImageUrl");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    Measurement.endAggregated("demoTestAggregated", "FirstImageUrl");
                    Measurement.endAggregated("demoTestAggregated", "SecondImageUrl");
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
        }).start();
```


## Changelog <a name="Changelog"></a>

### 0.1.0 [in progress]

- Initial version of plugin