package com.rakuten.tech.mobile.perf.rewriter

import com.rakuten.tech.mobile.perf.rewriter.classes.ClassJar
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static com.rakuten.tech.mobile.perf.TestUtil.resourceFile;

public class PerformanceTrackingRewriterSpec {
    @Rule public final TemporaryFolder projectDir = new TemporaryFolder()
    PerformanceTrackingRewriter performanceTrackingRewriter

    @Before def void setup() {
        performanceTrackingRewriter = new PerformanceTrackingRewriter();
        performanceTrackingRewriter.compileSdkVersion = "android-23"
        performanceTrackingRewriter.classpath = resourceFile("android23.jar").absolutePath
        performanceTrackingRewriter.input = resourceFile("user-TestUI.jar").absolutePath
        performanceTrackingRewriter.outputJar = projectDir.newFile("output.jar")
        performanceTrackingRewriter.tempJar = projectDir.newFile("temp.jar")
    }

    @Test def void "should copy the content of input jar file to output jar file with transformation"() {
        performanceTrackingRewriter.rewrite()

        ClassJar temp = new ClassJar(new File(performanceTrackingRewriter.outputJar));
        assert !temp.hasClass("com.rakuten.tech.mobile.perf.core.base.ActivityBase")
        assert !temp.hasClass("com.rakuten.tech.mobile.perf.core.mixins.ActivityMixin")
    }
}