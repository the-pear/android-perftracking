package com.rakuten.tech.mobile.perf.rewriter.mixins

import com.rakuten.tech.mobile.perf.rewriter.base.Base
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassJar
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassProvider
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassWriter
import org.gradle.api.logging.Logging
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.mockito.Mockito
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.Type

import static com.rakuten.tech.mobile.perf.TestUtil.resourceFile

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
        Mixin mixinTest = mixinLoader.loadMixin(classNode)
        Class clazz = provider.getClass("com.rakuten.tech.mobile.perf.core.mixins.AdapterViewOnItemClickListenerMixin")
        ClassReader reader = jar.getClassReader("com.rakuten.tech.mobile.perf.core.mixins.AdapterViewOnItemClickListenerMixin")
        ClassVisitor visitor = mixinTest.rewrite(clazz, writer)
        ClassVisitor visitorSpy = Mockito.spy(visitor)
        reader.accept(visitorSpy, 0)
        Mockito.verify(visitorSpy).visit(Mockito.any(int), Mockito.any(int), Mockito.eq("com/rakuten/tech/mobile/perf/core/mixins/AdapterViewOnItemClickListenerMixin"), Mockito.any(), Mockito.any(String.class), Mockito.any(String[].class))
    }

    @Test def void "should invoke visit method on the provided class parameters, and visit instance fields if exists withing the method"() {
        ClassNode classNode = jar.getClassNode("com.rakuten.tech.mobile.perf.core.mixins.ActivityMixin")
        Mixin mixinTest = mixinLoader.loadMixin(classNode)
        Class clazz = provider.getClass("com.rakuten.tech.mobile.perf.core.mixins.ActivityMixin")
        ClassReader reader = jar.getClassReader("com.rakuten.tech.mobile.perf.core.mixins.ActivityMixin")
        ClassVisitor visitor = mixinTest.rewrite(clazz, writer)
        ClassVisitor visitorSpy = Mockito.spy(visitor)
        reader.accept(visitorSpy, 0)
        Mockito.verify(visitorSpy).visit(Mockito.any(int), Mockito.any(int), Mockito.eq("com/rakuten/tech/mobile/perf/core/mixins/ActivityMixin"), Mockito.any(), Mockito.any(String.class), Mockito.any(String[].class))
    }

    @Test def void "Should call add method of MixinField, If any Field Node exists in Mixin Object"() {
        ClassNode classNode = jar.getClassNode("com.rakuten.tech.mobile.perf.core.mixins.VolleyHurlStackMixin")

        List<FieldNode> fieldNodeList = new ArrayList<FieldNode>();
        FieldNode fieldNode = Mockito.mock(FieldNode.class)
        fieldNode.name = "testFieldName"
        fieldNode.desc = Type.OBJECT
        fieldNodeList.add(fieldNode)

        List<AnnotationNode> annotationNodeList = new ArrayList<AnnotationNode>()
        AnnotationNode annotationNode = Mockito.mock(AnnotationNode.class)
        annotationNode.desc = "Lcom/rakuten/tech/mobile/perf/core/annotations/AddField;"
        annotationNodeList.add(annotationNode)
        fieldNode.visibleAnnotations = annotationNodeList

        classNode.fields = fieldNodeList

        Mixin mixinTest = mixinLoader.loadMixin(classNode)

        MixinField mixinFieldSpy = Mockito.spy(mixinTest.fields.get(0))
        mixinTest.fields.add(0,mixinFieldSpy)

        Class clazz = provider.getClass("com.rakuten.tech.mobile.perf.core.mixins.VolleyHurlStackMixin")
        ClassReader reader = jar.getClassReader("com.rakuten.tech.mobile.perf.core.mixins.VolleyHurlStackMixin")
        ClassVisitor visitor = mixinTest.rewrite(clazz, writer)
        reader.accept(visitor, 0)
        Mockito.verify(mixinFieldSpy).add(Mockito.any(ClassVisitor.class))
    }

}