package com.rakuten.tech.mobile.perf.rewriter.mixins

import com.rakuten.tech.mobile.perf.rewriter.classes.ClassJar
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassProvider
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassWriter
import org.gradle.api.logging.Logging
import org.junit.Before
import org.junit.Test
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.tree.ClassNode

import static com.rakuten.tech.mobile.perf.TestUtil.resourceFile
import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.*

public class MixerSpec {
    ClassVisitor classVisitor
    Mixin mixin
    Mixer mixer
    ClassProvider classProvider

    @Before def void setup() {
        classProvider = new ClassProvider(resourceFile("Core.jar").absolutePath)
        ClassWriter classWriter = new ClassWriter(classProvider, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classVisitor = classWriter
        ClassJar jar = new ClassJar(resourceFile("user-TestUI.jar"))
        ClassNode classNode = jar.getClassNode("com.rakuten.tech.mobile.perf.core.mixins.VolleyHurlStackMixin")
        MixinLoader mixinLoader = new MixinLoader(Logging.getLogger(MixinLoaderSpec.simpleName))
        mixin = mixinLoader.loadMixin(classNode)
        mixer = new Mixer()
    }

    @Test def void "should call rewriter method if class type match"() {
        Mixin mixinMock = spy(mixin)
        mixer.add(mixinMock)
        Class<?> clazz = classProvider.getClass("com.android.volley.toolbox.HurlStack");

        mixer.rewrite(clazz, classVisitor)

        verify(mixinMock).rewrite(any(), any())
    }

    @Test def void "should not call rewriter method if class type mismatch"() {
        Mixin mixinMock = spy(mixin)
        mixer.add(mixinMock)
        Class<?> clazz = classProvider.getClass("com.rakuten.tech.mobile.perf.core.mixins.ActivityMixin");

        mixer.rewrite(clazz, classVisitor)

        verify(mixinMock, never()).rewrite(any(), any())
    }

    @Test def void "should still return the same class visitor if class type mismatch"() {
        mixer.add(mixin)
        Class<?> clazz = classProvider.getClass("com.rakuten.tech.mobile.perf.core.mixins.ActivityMixin");

        ClassVisitor classVisitor = mixer.rewrite(clazz, classVisitor)

        assert classVisitor == this.classVisitor
    }

    @Test def void "should return the a new class visitor if class type match"() {
        mixer.add(mixin)
        Class<?> clazz = classProvider.getClass("com.android.volley.toolbox.HurlStack");

        ClassVisitor classVisitor = mixer.rewrite(clazz, classVisitor)

        assert classVisitor != this.classVisitor
    }
}