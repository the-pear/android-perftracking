package jp.co.rakuten.sdtd.perf

import org.gradle.api.Plugin
import org.gradle.api.Project

import com.android.build.gradle.AppExtension

/**
 * Gradle plugin
 *
 */
class PerfPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def android = project.extensions.findByType(AppExtension)
        android.registerTransform(new PerfTrackingTransform(project))

        project.dependencies.compile project.MODULE_GROUP+':runtime:'+project.MODULE_VERSION+'-SNAPSHOT'
    }
}
