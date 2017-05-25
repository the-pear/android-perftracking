package com.rakuten.tech.mobile.perf.rewriter.classes

import com.rakuten.tech.mobile.perf.rewriter.PerformanceTrackingRewriter
import org.gradle.api.logging.Logging
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.objectweb.asm.tree.ClassNode

import static com.rakuten.tech.mobile.perf.TestUtil.*

class ClassTrimmerSpec {
    ClassTrimmer classTrimmer

    @Before
    void setup() {
        ClassProvider provider = new ClassProvider(resourceFile("user-TestUI.jar").absolutePath)
        classTrimmer = new ClassTrimmer("android-23", provider, Logging.getLogger(ClassTrimmerSpec.class.getName()))
    }

    @Test
    void "should trim methods which has annotation"() {
        ClassJar classJar = new ClassJar(resourceFile("user-TestUI.jar"))
        ClassNode classNode = classJar.getClassNode("com.rakuten.tech.mobile.perf.core.base.FragmentBase")
        int originalMethodSize = classNode.methods.size()
        classTrimmer.trim(classNode)
        assert classNode.methods.size() != originalMethodSize
    }

    @Test
    void "should not trim methods which does not have annotation"() {
        ClassJar classJar = new ClassJar(resourceFile("user-TestUI.jar"))
        ClassNode classNode = classJar.getClassNode("com.rakuten.tech.mobile.perf.core.base.WebChromeClientBase")
        int originalMethodSize = classNode.methods.size()
        classTrimmer.trim(classNode)
        assert classNode.methods.size() == originalMethodSize
    }
}