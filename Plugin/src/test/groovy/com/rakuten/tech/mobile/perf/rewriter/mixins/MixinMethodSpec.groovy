package com.rakuten.tech.mobile.perf.rewriter.mixins

import com.rakuten.tech.mobile.perf.rewriter.classes.ClassJar
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassProvider
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassWriter
import org.gradle.api.logging.Logging
import org.junit.Before
import org.junit.Test
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode

import static com.rakuten.tech.mobile.perf.TestUtil.resourceFile
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*

public class MixinMethodSpec {

    ClassJar jar
    ClassProvider provider
    MixinLoader mixinLoader
    ClassWriter writer

    @Before def void setup() {
        jar = new ClassJar(resourceFile("user-TestUI.jar"))
        provider = new ClassProvider(resourceFile("user-TestUI.jar").absolutePath)
        mixinLoader = new MixinLoader(Logging.getLogger(MixinMethodSpec.simpleName))
        writer = new ClassWriter(provider, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    }

    @Test def void "should invoke visit method on the provided class parameters"() {
        ClassNode classNode = jar.getClassNode("com.rakuten.tech.mobile.perf.core.mixins.AdapterViewOnItemClickListenerMixin")
        Mixin mixin = mixinLoader.loadMixin(classNode)
        Class clazz = provider.getClass("com.rakuten.tech.mobile.perf.core.mixins.AdapterViewOnItemClickListenerMixin")
        ClassReader reader = jar.getClassReader("com.rakuten.tech.mobile.perf.core.mixins.AdapterViewOnItemClickListenerMixin")
        ClassVisitor visitor = mixin.rewrite(clazz, writer)
        ClassVisitor visitorSpy = spy(visitor)

        reader.accept(visitorSpy, 0)

        verify(visitorSpy).visit(any(int), any(int), eq("com/rakuten/tech/mobile/perf/core/mixins/AdapterViewOnItemClickListenerMixin"), any(), any(String.class), any(String[].class))
    }

    @Test def void "should invoke visit method on the provided class parameters, and visit instance fields if exists withing the method"() {
        ClassNode classNode = jar.getClassNode("com.rakuten.tech.mobile.perf.core.mixins.ActivityMixin")
        Mixin mixin = mixinLoader.loadMixin(classNode)
        Class clazz = provider.getClass("com.rakuten.tech.mobile.perf.core.mixins.ActivityMixin")
        ClassReader reader = jar.getClassReader("com.rakuten.tech.mobile.perf.core.mixins.ActivityMixin")
        ClassVisitor visitor = mixin.rewrite(clazz, writer)
        ClassVisitor visitorSpy = spy(visitor)

        reader.accept(visitorSpy, 0)

        verify(visitorSpy).visit(any(int), any(int), eq("com/rakuten/tech/mobile/perf/core/mixins/ActivityMixin"), any(), any(String.class), any(String[].class))
    }

    @Test def void "Should call add method of MixinField, If any Field Node exists in Mixin Object"() {
        ClassNode classNode = jar.getClassNode("com.rakuten.tech.mobile.perf.core.mixins.VolleyHurlStackMixin")
        def fieldNodeList = []
        FieldNode fieldNode = mock(FieldNode.class)
        fieldNode.name = "testFieldName"
        fieldNode.desc = Type.OBJECT
        fieldNodeList.add(fieldNode)
        def annotationNodeList = []
        AnnotationNode annotationNode = new AnnotationNode("Lcom/rakuten/tech/mobile/perf/core/annotations/AddField;")
        annotationNodeList.add(annotationNode)
        fieldNode.visibleAnnotations = annotationNodeList
        classNode.fields = fieldNodeList
        Mixin mixin = mixinLoader.loadMixin(classNode)
        MixinField mixinFieldSpy = spy(mixin.fields.get(0))
        mixin.fields.add(0,mixinFieldSpy)
        Class clazz = provider.getClass("com.rakuten.tech.mobile.perf.core.mixins.VolleyHurlStackMixin")
        ClassReader reader = jar.getClassReader("com.rakuten.tech.mobile.perf.core.mixins.VolleyHurlStackMixin")
        ClassVisitor visitor = mixin.rewrite(clazz, writer)

        reader.accept(visitor, 0)

        verify(mixinFieldSpy).add(any(ClassVisitor.class))
    }
}