package jp.co.rakuten.sdtd.perf

import org.gradle.api.Plugin
import org.gradle.api.Project


/**
 * Gradle plugin
 *
 */
class PerfPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        //target.android.registerTransform(new PerfTrackingTransform())
    }
}