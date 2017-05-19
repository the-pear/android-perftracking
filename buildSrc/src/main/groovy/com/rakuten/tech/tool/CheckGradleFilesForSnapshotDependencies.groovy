package com.rakuten.tech.tool

import groovy.io.FileType
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

import static com.rakuten.tech.tool.Functions.findAllSnapshots

@SuppressWarnings("GroovyUnusedDeclaration")
public class CheckGradleFilesForSnapshotDependencies extends DefaultTask {
    def exclude

    CheckGradleFilesForSnapshotDependencies() {
        description = "Check build files for SNAPSHOT dependencies"
        group = 'Verification'
    }

    @TaskAction
    void findAndCheckBuildFiles() {

        println "Checking gradle build files for "

        def visitGradleFile = {
            print "checking $it... "
            def script = new GradleBuildScript(it)
            if(script.compileDependencies) {
                def snapshots = findAllSnapshots(script.compileDependencies)
                logger.debug("Found compile dependencies: $script.compileDependencies with snapshots: $snapshots")
                if(snapshots) {
                    println "✗"
                    throw new GradleException(
                            "Found SNAPSHOT in compile dependencies in build script $it on $snapshots")
                }
            }

            if(script.buildScriptDependencies) {
                def snapshots = findAllSnapshots(script.buildScriptDependencies)
                logger.debug("Found buildscript dependencies: $script.buildScriptDependencies with snapshots: $snapshots")
                if(snapshots) {
                    println "✗"
                    throw new GradleException(
                            "Found SNAPSHOT in buildscript dependencies in build script $it on $snapshots")
                }
            }
            println "✓"
        }

        project.rootDir.traverse type: FileType.FILES,
                visit: visitGradleFile,
                nameFilter: ~/.*\.gradle$/,
                excludeFilter: exclude

        println "No SNAPSHOTS found ✓"
    }

}

