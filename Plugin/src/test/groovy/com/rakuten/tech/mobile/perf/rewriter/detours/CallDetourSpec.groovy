package com.rakuten.tech.mobile.perf.rewriter.detours

import org.gradle.api.logging.Logging
import org.junit.Before
import org.junit.Test
import org.objectweb.asm.MethodVisitor

import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify

public class CallDetourSpec {
    CallDetour callDetour

    @Before void setUp() {
        callDetour = new CallDetour(Logging.getLogger("test"))
    }

    @Test void "test"() {
        callDetour.owner = "java.lang.Object"

        boolean match = callDetour.matchOwner("", String.class)

        assert match
    }

    @Test void "test1"() {
        callDetour.owner = "java.lang.class"

        boolean match = callDetour.matchOwner("", String.class)

        assert !match
    }

    @Test void "test2"() {
        callDetour.detourOwner = "java.lang.String"
        callDetour.detourDesc = "detourDesc"
        MethodVisitor methodVisitorMock = mock(MethodVisitor.class)

        callDetour.rewrite(methodVisitorMock, (int)0, "java.lang.Object", Object.class, "name", "desc", true)

        verify(methodVisitorMock).visitMethodInsn(eq(184), eq("java.lang.String"), eq("name"), eq("detourDesc"), eq(false))
    }
}