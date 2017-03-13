package jp.co.rakuten.sdtd.runtime.internal;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import jp.co.rakuten.sdtd.perf.BuildConfig;
import jp.co.rakuten.sdtd.perf.runtime.internal.TrackingManager;

/**
 * Test {@link jp.co.rakuten.sdtd.perf.runtime.internal.TrackingManager} class.
 * @author RMSDK team(prj-rmsdk@mail.rakuten.com)
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/test/AndroidManifest.xml", sdk = 21, constants = BuildConfig.class)
public class TrackingManagerTest {

    @Test
    public void testAggregatedEquals() throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {

        Comparable comparable = new Comparable<Object>() {
            @Override
            public int compareTo(Object o) {
                return this.compareTo(o);
            }
        };
        String measurementId = "test";
        String measurementId2 = "test2";

        Class<?> outerClass = Class.forName("jp.co.rakuten.sdtd.perf.runtime.internal.TrackingManager");
        Constructor<?> outerConstructor = outerClass.getDeclaredConstructor();
        outerConstructor.setAccessible(true);
        TrackingManager outerObject = (TrackingManager) outerConstructor.newInstance();

        outerObject.startAggregated(measurementId, comparable);
        outerObject.startMeasurement(measurementId2);

        Field field = outerObject.getClass().getDeclaredField("mAggregatedDataMap");
        field.setAccessible(true);
        HashMap data = (HashMap)field.get(outerObject);

        Class<?> innerClass = TrackingManager.class.getDeclaredClasses()[0];
        Constructor<?> constructor = innerClass.getDeclaredConstructors()[1];
        constructor.setAccessible(true);
        Object inner = constructor.newInstance(outerObject,measurementId, comparable);
        Object inner2 = constructor.newInstance(outerObject,measurementId2, null);
        Assert.assertTrue(data.containsKey(inner));
        Assert.assertTrue(data.containsKey(inner2));
    }

}
