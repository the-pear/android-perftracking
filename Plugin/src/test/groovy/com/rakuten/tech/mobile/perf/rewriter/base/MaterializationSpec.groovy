package com.rakuten.tech.mobile.perf.rewriter.base

import com.rakuten.tech.mobile.perf.rewriter.classes.ClassJar
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassProvider
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassWriter
import org.gradle.api.logging.Logging
import org.junit.Before
import org.junit.Test
import org.objectweb.asm.ClassReader

import static com.rakuten.tech.mobile.perf.TestUtil.*

class MaterializationSpec {
    def index = 1
    File jar
    Materialization materialization

    Class clazz
    ClassWriter writer
    ClassReader reader

    // Step 1: select class to rewrite in materialization:
    String targetClass = "jp.co.rakuten.sdtd.user.authenticator.AuthenticatorFederatedActivity"

    @Before void setup() {
        // Step 2: define a base class description
        def base = new Base();
        def classpath = resourceFile("user-TestUI.jar").absolutePath + File.pathSeparator +
                resourceFile("android23.jar").absolutePath
        def provider = new ClassProvider(classpath);
        def logger = Logging.getLogger("test")

        clazz = provider.getClass(targetClass)
        reader = new ClassJar(resourceFile("user-TestUI.jar")).getClassReader(targetClass)
        writer = new ClassWriter(provider, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

        materialization = new Materialization(base, index++, provider, logger);
        jar = resourceFile("user-TestUI.jar");
    }

    @Test void "should insert super class in inheritance hierarchy"() {
        def visitor = materialization.rewrite(clazz, writer)
        // Step 3: debug Materialization as it rewrites the target class
        reader.accept(visitor, 0)
        // TODO add verifications
    }

}