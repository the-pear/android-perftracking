package com.rakuten.tech.mobile.perf.rewriter

import com.rakuten.tech.mobile.perf.rewriter.classes.ClassJar
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode

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
        assert temp.hasClass("com.rakuten.tech.mobile.perf.core.base.ActivityBase")
        assert temp.hasClass("com.rakuten.tech.mobile.perf.core.mixins.ActivityMixin")
    }

    @Test def void "should rewrite the enable value and add the class file to output JAR"() {
        dummyRewriter.input = resourceFile("TestAppPerformanceConfig.jar").absolutePath

        dummyRewriter.rewrite()

        ClassJar temp = new ClassJar(new File(dummyRewriter.outputJar));
        ClassNode classNode = temp.getClassNode("com.rakuten.tech.mobile.perf.runtime.internal.AppPerformanceConfig")
        List<FieldNode> fieldNodeList = classNode.fields
        for (FieldNode fieldNode : fieldNodeList) {
            if (fieldNode.name == "enabled") {
                assert fieldNode.value == 0
            }
        }
    }
}