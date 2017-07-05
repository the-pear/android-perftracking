package com.rakuten.tech.mobile.perf.rewriter.mixins

import org.junit.Test
import org.mockito.Mockito
import org.objectweb.asm.tree.FieldNode
import org.gradle.api.logging.Logging
import org.objectweb.asm.ClassVisitor
import org.junit.Before


public class MixinFieldSpec {
    MixinField mixinField

    @Before def void setup() {
        FieldNode fieldNode = Mockito.mock(FieldNode.class)
        fieldNode.name = "test_name"
        fieldNode.access = 0
        fieldNode.desc = "test_desc"
        fieldNode.signature = "test_signature"
        fieldNode.value = new Integer(1)
        mixinField = new MixinField(Logging.getLogger(MixinLoaderSpec.simpleName), fieldNode)
    }

    @Test def void "should visit the field with the provided class visitor"() {
        ClassVisitor classVisitor = Mockito.mock(ClassVisitor.class)
        Mockito.when(classVisitor.visitField(Mockito.any(int), Mockito.any(String.class),
                Mockito.any(String.class), Mockito.any(String.class), Mockito.any(Object.class))).thenReturn(null)
        mixinField.add(classVisitor)
        Mockito.verify(classVisitor,Mockito.times(1))visitField(Mockito.any(int),
                Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(Object.class))
    }

}