package com.rakuten.tech.mobile.perf.rewriter.detours

import com.rakuten.tech.mobile.perf.rewriter.classes.ClassJar
import org.gradle.api.logging.Logging
import org.junit.Before
import org.junit.Test;
import org.objectweb.asm.tree.ClassNode

import static com.rakuten.tech.mobile.perf.TestUtil.*

public class DetourLoaderSpec {
    DetourLoader detourLoader

    @Before void setUp() {
        detourLoader = new DetourLoader(Logging.getLogger("test"))
    }

    @Test void "test"() {
        ClassJar jar = new ClassJar(resourceFile("user-testUI.jar"))
        ClassNode classNode = jar.getClassNode("com.rakuten.tech.mobile.perf.core.detours.URLDetours")
        detourLoader.load(classNode)
    }
}