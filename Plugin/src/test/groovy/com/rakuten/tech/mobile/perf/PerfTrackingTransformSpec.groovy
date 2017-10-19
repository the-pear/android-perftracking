package com.rakuten.tech.mobile.perf

import com.android.build.api.transform.Context
import com.android.build.api.transform.TransformOutputProvider
import com.rakuten.tech.mobile.perf.rewriter.DummyRewriter
import com.rakuten.tech.mobile.perf.rewriter.PerformanceTrackingRewriter
import groovy.mock.interceptor.MockFor
import org.gradle.api.Project
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

public class PerfTrackingTransformSpec extends UnitSpec {

    // Mocks
    Context ctx
    TransformOutputProvider outputProvider

    // System under test
    PerfTrackingTransform transform

    @Rule public final TemporaryFolder tempDir = new TemporaryFolder()

    @Before public void setup() {
        def android = [
                bootClasspath: [""],
                compileSdkVersion: "android-23"
        ]
        def project = new MockFor(Project)
        project.ignore('android') { android }
        project.ignore('getAndroid') { android }
        transform = new PerfTrackingTransform(project.proxyInstance())

        ctx = [getTemporaryDir: { tempDir.getRoot() }] as Context
        outputProvider = [
                getContentLocation: { name, types, scopes, format ->
                    new File(tempDir.root, "outputJar")
                }
        ] as TransformOutputProvider
    }

    @Test public void "should use performance tracking rewriter when enabled"() {
        def (inputs, referenceInputs) = [[], []]
        transform.enableRewrite = true

        transform.transform(ctx, inputs, referenceInputs, outputProvider, false)

        assert transform.rewriter instanceof PerformanceTrackingRewriter
    }

    @Test public void "should use dummy rewriter when disabled"() {
        def (inputs, referenceInputs) = [[], []]
        transform.enableRewrite = false

        transform.transform(ctx, inputs, referenceInputs, outputProvider, false)

        assert transform.rewriter instanceof DummyRewriter
    }
}
