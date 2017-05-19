package com.rakuten.tech.mobile.perf

import org.gradle.api.Project
import org.gradle.api.internal.ClosureBackedAction
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertNotNull

public class PerfPluginTest {
    Project project

    @Before
    public void setUp() {
        project = ProjectBuilder.builder().build()
        assertNotNull(project);
        project.pluginManager.apply 'com.android.application'
    }

    @Test
    public void testPluginIsAppliedWithOutError() {
        project.pluginManager.apply 'com.rakuten.tech.mobile.perf'
        assertTrue(project.pluginManager.hasPlugin("com.rakuten.tech.mobile.perf"))
        PerfPluginExtension extension = project.extensions.getByName("performanceTracking")
        assertTrue(extension instanceof PerfPluginExtension)
    }

}