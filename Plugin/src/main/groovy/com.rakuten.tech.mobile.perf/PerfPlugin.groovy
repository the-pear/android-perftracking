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
    PerfTrackingTransform perfTrackingTransform;

    @Override
    void apply(Project project) {
        def info = new Properties()
        info.load(PerfPlugin.class.classLoader.getResourceAsStream('info.properties'))

        def version = info.getProperty('version')
        def runtime = info.getProperty('runtime')
        def repository = info.getProperty('repository')

        perfTrackingTransform = new PerfTrackingTransform(project)

        def android = project.extensions.findByType(AppExtension)
        android.registerTransform(perfTrackingTransform)

        // disable performance tracking for debug
        project.gradle.taskGraph.beforeTask { Task task ->
            if (task.name.startsWith("transformClassesWithPerfTrackingFor")) {
                perfTrackingTransform.setEnableReWrite(!task.name.contains("Debug"))
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
