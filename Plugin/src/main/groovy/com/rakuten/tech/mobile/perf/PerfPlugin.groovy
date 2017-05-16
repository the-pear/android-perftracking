package com.rakuten.tech.mobile.perf

import com.android.build.gradle.AppExtension
import org.gradle.api.NamedDomainObjectContainer
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

        // Create NamedDomainObjectContainer instance for
        // a collection of Product objects
        NamedDomainObjectContainer<PerfPluginExtension> perfPluginExtensionContainer =
                project.container(PerfPluginExtension)

        // Add the container instance to our project
        // with the name products.
        project.extensions.add('performanceTracking', perfPluginExtensionContainer)

        def version = info.getProperty('version')
        def runtime = info.getProperty('runtime')
        def repository = info.getProperty('repository')

        perfTrackingTransform = new PerfTrackingTransform(project)

        def android = project.extensions.findByType(AppExtension)
        android.registerTransform(perfTrackingTransform)

        // disable/Enabling performance tracking or build Type
        def build = project.extensions.getByName('performanceTracking')
        project.gradle.taskGraph.beforeTask { Task task ->
            if (task.name.startsWith("transformClassesWithPerfTrackingFor")) {
                build.all{
                    if (task.name.toLowerCase().contains(name)){
                        mPerfTrackingTransform.setEnableReWrite(enable)
                    }
                }
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
