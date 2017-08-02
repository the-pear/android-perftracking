# Performance Tracking

The Performance Tracking module enables applications to measure the time taken for executing user scenarios like searching products and displaying product details.
Each scenario/metric lists various method, network call measurements. Performance Tracking even provides api's for starting custom measurements.

## Table of Contents
* [Install Perf Tracking SDK](#install)
* [Customize Tracking](#customize)
* [Control Instrumentation/Tracking](#control)
* [Enable Debug Logs](#debug)
* [How to Confirm Performance Tracking SDK Integration](#integration)
* [Changelog](#changelog)

##  <a name="install"></a> Installation procedure

### REMS Performance Tracking Credentials

Your app must be registered in the [Relay Portal](https://rs-portal-web-prd-japaneast-wa.azurewebsites.net/) to use the App Performance Tracking feature.
Request for an App Performance Tracking Subscription Key through the [API Portal](https://remsapijapaneast.portal.azure-api.net) with your application's package name.
If you have any questions, please visit our [Developer Portal](https://developers.rakuten.com/hc/en-us/categories/115001441467-Relay) or you may contact us through the [Inquiry Form](https://developers.rakuten.com/hc/en-us/requests/new?ticket_form_id=399907)

### #1 Add dependency to buildscript

```groovy
buildscript {
    repositories {
        maven { url 'http://artifactory.raksdtd.com/artifactory/libs-release' }
    }
    dependencies {
        classpath 'com.rakuten.tech.mobile.perf:plugin:0.1.1'
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

SDK instruments the application at compile time (currently in build types other than `debug`). To control application instrumentation or tracking see [Control Instrumentation](#control).
So when you build your app you will see a `transformClassesWithPerfTrackingXxx` task

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

Now your application is ready to automatically track the launch metrics, network requests, view lifecycle methods, runnables, webview loads, onClick listeners, threads, volley's hurl stack and many more. To add custom measurement and structure them around metrics see [Customize Tracking](#customize).

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
* Other metrics are started by the app by calling `Metric#startMetric(String)`

**What makes a metric keep going:**

* Activity life cycle changes
* Fragment life cycle and visibility changes
* Loading a page in WebView

**What makes a metric terminate:**

* User interactions like clicks, back button presses, etc.
* WebView finishes loading a page
* Timeout of 10 seconds
* A new metric is started start

## <a name="debug"></a> Enable Debug Logs

```xml
  <manifest>
      <application>
          <meta-data android:name="com.rakuten.tech.mobile.perf.debug"
                     android:value="true" />
      </application>
  </manifest>
```

You can see logs by filtering with "Performance Tracking" tag.

## <a name="control"></a> Control Instrumentation

As mentioned above SDK instruments the application at compile time. Currently instrumentation is disabled in `debug` build type, which means tracking is disabled in `debug` build by default.
You can enable/disable the instrumentation/tracking at build time in different build types by adding following code to application's build.gradle. Here `enable` denotes instrumentation/tracking control switch at build time.

```
performanceTracking {
    release {
        enable = true
    }
    debug {
        enable = true
    }
    qa {
        enable = false
    }
}
```

## <a name="integration"></a> Confirm the Performance Tracking integration
### Check for Build

* Make sure `transformClassesWithPerfTrackingXXX` tasks are successful without any error during build process.
* If your build gets failed because of any error in `transformClassesWithPerfTrackingXXX` tasks you can contact us through [Inquiry Form](https://developers.rakuten.com/hc/en-us/requests/new?ticket_form_id=399907).
* You can proceed with build by disabling instrumentation/tracking as shown [Control Instrumentation](#control).

### Run your App

* On first run of your app after integrating Performance Tracking the module will only fetch and store its configuration data, it **will not** send metric data. On subsequent runs the module will track and send metrics and measurements if the previously received configuration is valid and the enable percentage check succeeds.

### Check Configuration

* Using charles or any other network capture software you can verify whether configuration loaded successfully or not?
* Look for `https://api.apps.global.rakuten.com` in charles and check if Response code if `200 OK` for successful configuration.
* If Response code is not 200, then please check your subscription key in AndroidManifest.xml.
* You can even verify this by enabling debug logs as shown in [Enable Debug Logs](#debug). You will see "Error loading configuration" log in failure scenario.

### Check Sending data to eventhub

* Tracking data of your app will get reflected in the relay portal after few hours.
* By using charles or any network capture software you can confirm whether tracking data is send to eventhub or not before it gets reflected in relay portal.
* Look for `https://perf-eventhub-prd-japaneast.servicebus.windows.net` in charles and check Response code is `201 Created` for `measurements`.
* You can even verify this by enabling debug logs as shown in [Enable Debug Logs](#debug). You will see "SEND_METRIC" AND "SEND" in logs.

## <a name="changelog"></a> Changelog

### 0.1.1

- Post MVP Release

### 0.1.0

- MVP Release
