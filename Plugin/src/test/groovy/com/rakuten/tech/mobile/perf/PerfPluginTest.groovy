package com.rakuten.tech.mobile.perf

import org.gradle.api.Project
import org.junit.Test
import org.gradle.testfixtures.ProjectBuilder;
import static org.junit.Assert.assertNotNull;


class PerfPluginTest {
    @Test
    public void testPerfPlugin() {
        Project project = ProjectBuilder.builder().build()
        assertNotNull(project);
        project.pluginManager.apply 'com.android.application'
        project.pluginManager.apply 'com.rakuten.tech.mobile.perf'

        //assertTrue(project.tasks.hello instanceof PerfPlugin)
    }
}