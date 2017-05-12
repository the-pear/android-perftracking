package com.rakuten.tech.mobile.perf

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Gradle plugin
 *
 */
class PerfPlugin implements Plugin<Project> {
    PerfTrackingTransform mPerfTrackingTransform;

    @Override
    void apply(Project project) {
        def info = new Properties()
        info.load(PerfPlugin.class.classLoader.getResourceAsStream('info.properties'))

        def version    = info.getProperty('version')
        def runtime    = info.getProperty('runtime')
        def repository = info.getProperty('repository')

        mPerfTrackingTransform = new PerfTrackingTransform(project, true)

        def android = project.extensions.findByType(AppExtension)
        android.registerTransform(mPerfTrackingTransform)

        // disable performance tracking for debug
        project.gradle.taskGraph.beforeTask { Task task ->
            if (task.name.startsWith("transformClassesWithPerfTrackingForDebug")) {
                mPerfTrackingTransform.setEnableReWrite(false)
            } else {
                mPerfTrackingTransform.setEnableReWrite(true)
            }
        }

        project.configure(project) {
            repositories {
                maven {
                    url repository
                }
            }
        }

        project.dependencies.compile runtime
    }
}
