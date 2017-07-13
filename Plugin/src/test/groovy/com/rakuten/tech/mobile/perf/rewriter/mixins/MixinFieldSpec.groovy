package com.rakuten.tech.mobile.perf.rewriter.mixins

import org.gradle.api.logging.Logging
import org.junit.Before
import org.junit.Test
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.tree.FieldNode

import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.*

public class MixinFieldSpec {
    MixinField mixinField

    @Before def void setup() {
        FieldNode fieldNode = new FieldNode(0, "test_name", "test_desc", "test_signature", new Integer(1))
        mixinField = new MixinField(Logging.getLogger(MixinLoaderSpec.simpleName), fieldNode)
    }

    @Test def void "should visit the field with the provided class visitor, when mixin field's add method is invoked"() {
        ClassVisitor classVisitorMock = mock(ClassVisitor.class)
        when(classVisitorMock.visitField(any(int), any(String.class),
                any(String.class), any(String.class), any(Object.class))).thenReturn(null)

        mixinField.add(classVisitorMock)

        verify(classVisitorMock).visitField(any(int),
                any(String.class), any(String.class), any(String.class), any(Object.class))
    }

}