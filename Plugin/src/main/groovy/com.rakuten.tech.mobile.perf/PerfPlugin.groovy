package com.rakuten.tech.mobile.perf

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
        def info = new Properties()
        info.load(PerfPlugin.class.classLoader.getResourceAsStream('info.properties'))

        def version    = info.getProperty('version')
        def runtime    = info.getProperty('runtime')
        def repository = info.getProperty('repository')

        def android    = project.extensions.findByType(AppExtension)
        android.registerTransform(new PerfTrackingTransform(project))

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
