# Performance Tracking

The Performance Tracking module enables applications to measure the time taken for executing user scenarios like searching products and displaying product details.
Each scenario/metric lists various method, network call measurements. Performance Tracking even provides api's for starting custom measurements.

## Table of Contents

* [Install Perf Tracking SDK](#install)
* [Customize Tracking](#customize)
* [Changelog](#changelog)

##  <a name="install"></a> Installation procedure

### REMS Performance Tracking Credentials

Your app must be registered in the [Relay Portal](https://rs-portal-web-prd-japaneast-wa.azurewebsites.net/) to use the App Performance Tracking feature.
Request for an App Performance Tracking Subscription Key through the [Inquiry Form](https://developers.rakuten.com) with your application's package name.

If you have any questions, please visit our [Documentation Portal](https://developers.rakuten.com/hc/en-us/categories/115000711608-Rakuten-Ecosystem-Mobile-REM-) or you may contact us through the [Inquiry Form](https://developers.rakuten.com)

### #1 Add dependency to buildscript

```groovy
buildscript {
    repositories {
        maven { url 'http://artifactory.raksdtd.com/artifactory/libs-release' }
    }
    dependencies {
        classpath 'com.rakuten.tech.mobile.perf:plugin:0.1.0'
    }
}

apply plugin: 'com.rakuten.tech.mobile.perf'
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

### #3 Build Application

The SDK instruments the application at compile time (currently in build types other than `debug`). So when you build your app you will see a `transformClassesWithPerfTracking` task

```bash
$ ./gradlew assembleRelease
:preBuild UP-TO-DATE
:preReleaseBuild UP-TO-DATE
:compileReleaseAidl
# etc...
:transformClassesWithPerfTrackingForRelease
:transformClassesWithDexForRelease
:transformResourcesWithMergeJavaResForRelease
:packageRelease
:assembleRelease

BUILD SUCCESSFUL
```

Now your application is ready to automatically track the launch metrcis, network requests, view lifecycle methods, runnables, webview loads, onClick listeners, threads, volley's hurl stack and many more. To add custom measurement and structure them around metrics see [Customize Tracking](#customize). 

You will see your measurements in the [Relay Portal](https://rs-portal-web-prd-japaneast-wa.azurewebsites.net/) under Features > App Performance. If you obfuscate your app you can upload the `mapping.txt` in the portal and the tracking data will be deobfuscated for you.

## <a name="customize"></a> Customize Tracking

### Metrics

The Performance Tracking SDK is build around the concepts of **Metrics** - they measure a single event from the user perspective. Examples of metrics are app launch time, a detail screen load time or search result load time. 

#### Starting Metrics

To start a metric use the `Metric` API:

```java
@Override public void onCreate(Bundle savedInstanceState) {
    Metric.start(StandardMetric.ITEM.getValue());
}
```

Currently there can only be one active metric at any given point of time, so if you start another metric the first metric will be considered done.

```java
Metric.start(StandardMetric.ITEM.getValue()); 
Metric.start(StandardMetric.SEACH.getValue()); // at this point the ITEM metric is considered done    
```

**NOTE:** The launch metric is started automatically by the SDK.

```java
// Custem Metric metric name can be AlphaNumeric, -, _, . and <i>Space</i>.
Metric.start("my_custom_metric");
```

#### <a name="termination"></a> Automatic Metric Termination

Metrics terminate automatically according to a set of rules described below. That means developers are only required to start metrics and the SDK takes care of the rest.

**What makes a metric start:**

* The `StandardMetric.LAUNCH` metric is started automatically by the SDK 
* Other metrics are started by tha app by calling `Metric#startMetric(String)`

**What makes a metric keep going:**

* Activity life cycle changes
* Fragment life cycle and visibility changes
* Loading a page in WebView

**What makes a metric terminate:**

* User interactions like clicks, back button presses, etc.
* WebView finishes loading a page
* Timeout of 10 seconds
* A new metric is started start

## <a name="changelog"></a> Changelog

### 0.1.0

- MVP Release