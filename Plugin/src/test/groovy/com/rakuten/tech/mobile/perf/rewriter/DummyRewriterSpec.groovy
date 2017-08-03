package com.rakuten.tech.mobile.perf.rewriter

import com.rakuten.tech.mobile.perf.rewriter.classes.ClassJar
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode

import static com.rakuten.tech.mobile.perf.TestUtil.resourceFile;

public class DummyRewriterSpec {
    @Rule public final TemporaryFolder projectDir = new TemporaryFolder()
    DummyRewriter dummyRewriter

    @Before def void setup() {
        dummyRewriter = new DummyRewriter();
        dummyRewriter.compileSdkVersion = "android-23"
        dummyRewriter.classpath = resourceFile("android23.jar").absolutePath
        dummyRewriter.input = resourceFile("user-TestUI.jar").absolutePath
        dummyRewriter.outputJar = projectDir.newFile("output.jar")
        dummyRewriter.tempJar = projectDir.newFile("temp.jar")
    }

    @Test def void "should copy the content of input jar file to output jar file with out transformation"() {
        dummyRewriter.rewrite()

        ClassJar temp = new ClassJar(new File(dummyRewriter.outputJar));
        ClassNode classNode = temp.getClassNode("jp.co.rakuten.sdtd.user.ui.BaseActivity")
        List<MethodNode> methodNodes = classNode.methods
        boolean hasMethod = false
        for (MethodNode methodNode : methodNodes) {
            if (methodNode.name == "com_rakuten_tech_mobile_perf_onCreate") {
                hasMethod = true
            }
        }
        assert !hasMethod
    }

    @Test def void "should rewrite AppPerformanceConfig class, set enable value to false and add to output JAR"() {
        dummyRewriter.input = resourceFile("TestAppPerformanceConfig.jar").absolutePath

        dummyRewriter.rewrite()

        ClassJar temp = new ClassJar(new File(dummyRewriter.outputJar));
        assert temp.hasClass("com.rakuten.tech.mobile.perf.runtime.internal.AppPerformanceConfig")
        ClassNode classNode = temp.getClassNode("com.rakuten.tech.mobile.perf.runtime.internal.AppPerformanceConfig")
        assert classNode.fields.size() > 0
        List<FieldNode> fieldNodeList = classNode.fields
        boolean hasEnabledField = false
        boolean isEnableFieldFalse = true
        for (FieldNode fieldNode : fieldNodeList) {
            if (fieldNode.name == "enabled") {
                hasEnabledField = true
                isEnableFieldFalse = fieldNode.value
            }
        }
        assert hasEnabledField
        assert !isEnableFieldFalse
    }
}