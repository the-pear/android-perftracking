package com.rakuten.tech.mobile.perf.rewriter.detours

import org.gradle.api.logging.Logging
import org.junit.Before
import org.junit.Test
import org.objectweb.asm.MethodVisitor

import static org.mockito.Mockito.*

public class StaticCallDetourSpec {
    StaticCallDetour staticCallDetour

    @Before void setUp() {
        staticCallDetour = new StaticCallDetour(Logging.getLogger("test"))
    }

    @Test void "test"() {
        staticCallDetour.owner = "java.lang.Object"

        boolean match = staticCallDetour.matchOwner("java.lang.Object", Object.class)

        assert match
    }

    @Test void "test1"() {
        staticCallDetour.owner = "java.lang.class"

        boolean match = staticCallDetour.matchOwner("java.lang.Object", Object.class)

        assert !match
    }

    @Test void "test2"() {
        staticCallDetour.detourOwner = "java.lang.String"
        MethodVisitor methodVisitorMock = mock(MethodVisitor.class)

        staticCallDetour.rewrite(methodVisitorMock, (int)0, "java.lang.Object", Object.class, "name", "desc", true)

        verify(methodVisitorMock).visitMethodInsn(eq(184), eq("java.lang.String"), eq("name"), eq("desc"), eq(false))
    }
}