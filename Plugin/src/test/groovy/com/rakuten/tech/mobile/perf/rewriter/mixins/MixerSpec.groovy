package com.rakuten.tech.mobile.perf.rewriter.mixins

import com.rakuten.tech.mobile.perf.rewriter.classes.ClassProvider
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassWriter
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.objectweb.asm.ClassVisitor

import static com.rakuten.tech.mobile.perf.TestUtil.resourceFile

public class MixerSpec {
    ClassProvider providerTest
    ClassVisitor classVisitorTest

    @Before def void setup() {
        providerTest = new ClassProvider(resourceFile("user-TestUI.jar").absolutePath)
        ClassWriter classWriterTest = new ClassWriter(providerTest, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classVisitorTest = classWriterTest
    }

    @Test def void "should call rewiter method if class type match and return the provided ClassVisitor object"() {
        Mixin mixin = Mockito.mock(Mixin.class)
        Mockito.when(mixin.match(Mockito.any())).thenReturn(true)
        Mockito.when(mixin.rewrite(Mockito.any(), Mockito.any())).thenReturn(classVisitorTest)

        Mixer mixerTest = new Mixer()
        mixerTest.add(mixin)

        assert mixerTest.rewrite(Object.class, classVisitorTest).equals(classVisitorTest)
        Mockito.verify(mixin, Mockito.times(1)).match(Mockito.any())
        Mockito.verify(mixin, Mockito.times(1)).rewrite(Mockito.any(), Mockito.any())
    }

    @Test def void "should not call rewriter method if class type mis match, but still return the provided ClassVisitor object"() {
        Mixin mixin = Mockito.mock(Mixin.class)
        Mockito.when(mixin.match(Mockito.any())).thenReturn(false)
        Mockito.when(mixin.rewrite(Mockito.any(), Mockito.any())).thenReturn(classVisitorTest)

        Mixer mixerTest = new Mixer()
        mixerTest.add(mixin)

        assert mixerTest.rewrite(Object.class, classVisitorTest).equals(classVisitorTest)
        Mockito.verify(mixin, Mockito.times(1)).match(Mockito.any())
        Mockito.verify(mixin, Mockito.times(0)).rewrite(Mockito.any(), Mockito.any())
    }
}