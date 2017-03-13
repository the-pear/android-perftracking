package jp.co.rakuten.sdtd.runtime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import jp.co.rakuten.sdtd.perf.BuildConfig;

import static org.junit.Assert.*;

/**
 * Dummy Unit test
 * Using RobolectricTestRunner for accessing test assets.
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/test/AndroidManifest.xml", sdk = 21, constants = BuildConfig.class)
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
}