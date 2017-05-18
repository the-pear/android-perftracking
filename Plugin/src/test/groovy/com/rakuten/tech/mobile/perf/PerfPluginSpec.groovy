package com.rakuten.tech.mobile.perf

import com.rakuten.tech.mobile.perf.rewriter.DummyRewriter
import com.rakuten.tech.mobile.perf.rewriter.PerformanceTrackingRewriter
import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.gradle.testkit.runner.TaskOutcome

public class PerfPluginSpec {
    @Rule public final TemporaryFolder projectDir = new TemporaryFolder(new File("tmp"))
    File buildFile

    @Before def void setup() {
        buildFile = projectDir.newFile('build.gradle')
        def main = projectDir.newFolder('src', 'main')
        def manifest = new File(main, "AndroidManifest.xml")
        manifest << resourceFile("manifest").text
    }

    @Test def void "plugin should add transformation tasks"() {
        buildFile << resourceFile("example_app").text
        def result = GradleRunner.create()
                .withProjectDir(projectDir.root)
                .withArguments('tasks', '--all')
                .build()

        assert result.output.contains("transformClassesWithPerfTrackingForDebug")
        assert result.output.contains("transformClassesWithPerfTrackingForRelease")
    }

    @Test def void "plugin should use dummy rewriter for debug build"() {
        buildFile << resourceFile("example_app").text
        def result = GradleRunner.create()
                .withProjectDir(projectDir.root)
                .withArguments('assembleDebug', '--debug')
                .build()

        assert result.task(':transformClassesWithPerfTrackingForDebug').outcome == TaskOutcome.SUCCESS
        assert !result.task(':transformClassesWithPerfTrackingForRelease')
        assert result.output.contains(DummyRewriter.canonicalName)
        assert !result.output.contains(PerformanceTrackingRewriter.canonicalName)
    }

    @Test def void "plugin should use real rewriter for release build"() {
        buildFile << resourceFile("example_app").text
        def result = GradleRunner.create()
                .withProjectDir(projectDir.root)
                .withArguments('assembleRelease', '--debug')
                .build()

        assert !result.task(':transformClassesWithPerfTrackingForDebug')
        assert result.task(':transformClassesWithPerfTrackingForRelease').outcome  == TaskOutcome.SUCCESS
        assert !result.output.contains(DummyRewriter.canonicalName)
        assert result.output.contains(PerformanceTrackingRewriter.canonicalName)
    }

    static def resourceFile(name) {
        new File("src/test/resources/$name")
    }

}
