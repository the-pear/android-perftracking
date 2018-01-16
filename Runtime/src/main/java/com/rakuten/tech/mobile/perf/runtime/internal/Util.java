package com.rakuten.tech.mobile.perf.runtime.internal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.security.auth.x500.X500Principal;

/**
 * Shared functions for Relay SDKs
 */
class Util {

  private static final String SUBSCRIPTION_META_KEY = "com.rakuten.tech.mobile.relay.SubscriptionKey";
  private static final String RELAY_APP_ID = "com.rakuten.tech.mobile.relay.AppId";
  private static final X500Principal DEBUG_DN = new X500Principal("C=US, O=Android, CN=Android Debug");

  /**
   * Extract the (shared) relay subscription key from the app's manifest. The key is expected as
   * shown below:
   *
   * ```xml
   * <manifest>
   *   <application>
   *     <meta-data android:name="com.rakuten.tech.mobile.relay.SubscriptionKey"
   *     android:value="subscriptionKey" />
   *   </application>
   * </manifest>
   * ```
   *
   * @param context application context
   * @return subscription key if present, null otherwise
   */
  static @Nullable String getSubscriptionKey(@NonNull Context context) {
    return getMeta(context, SUBSCRIPTION_META_KEY);
  }

  /**
   * Extract the relay app id from the app's manifest. The appId is expected as
   * shown below:
   *
   * ```xml
   * <manifest>
   *   <application>
   *     <meta-data android:name="com.rakuten.tech.mobile.relay.AppId"
   *     android:value="appId" />
   *   </application>
   * </manifest>
   * ```
   *
   * @param context application context
   * @return relay app id if present, null otherwise
   */
  static @Nullable String getRelayAppId(@NonNull Context context) {
    return getMeta(context, RELAY_APP_ID);
  }

  static @Nullable String getMeta(@NonNull Context context, @NonNull String key) {
    try {
      Bundle metaData = context.getPackageManager().getApplicationInfo(context.getPackageName(),
          PackageManager.GET_META_DATA).metaData;
      return metaData != null ? metaData.getString(key) : null;
    } catch (PackageManager.NameNotFoundException e) {
      return null;
    }
  }

  /**
   * Check if the application is debuggable.
   *
   * This method check if the application is signed by a debug key.
   * https://stackoverflow.com/questions/7085644/how-to-check-if-apk-is-signed-or-debug-build
   *
   * @param context application context
   * @return true if app is debuggable, false otherwise
   */
  static boolean isAppDebuggable(Context context)
  {
    boolean debuggable = false;

    try
    {
      PackageInfo pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
      Signature signatures[] = pinfo.signatures;

      CertificateFactory cf = CertificateFactory.getInstance("X.509");

      for ( int i = 0; i < signatures.length;i++)
      {
        ByteArrayInputStream stream = new ByteArrayInputStream(signatures[i].toByteArray());
        X509Certificate cert = (X509Certificate) cf.generateCertificate(stream);
        debuggable = cert.getSubjectX500Principal().equals(DEBUG_DN);
        if (debuggable)
          break;
      }
    }
    catch (PackageManager.NameNotFoundException e)
    {
      //debuggable variable will remain false
    }
    catch (CertificateException e)
    {
      //debuggable variable will remain false
    }
    catch (NullPointerException e)
    {
      //debuggable variable will remain false
    }
    return debuggable;
  }
}
