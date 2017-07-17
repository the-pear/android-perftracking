package com.rakuten.tech.mobile.perf.rewriter.detours

import org.gradle.api.logging.Logging
import org.junit.Before
import org.junit.Test
import org.objectweb.asm.MethodVisitor

import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify

public class ParameterDetourSpec {
    ParameterDetour parameterDetour

    @Before void setUp() {
        parameterDetour = new ParameterDetour(Logging.getLogger("test"))
    }

    @Test void "test"() {
        parameterDetour.owner = "java.lang.Object"

        boolean match = parameterDetour.matchOwner("", String.class)

        assert match
    }

    @Test void "test1"() {
        parameterDetour.owner = "java.lang.class"

        boolean match = parameterDetour.matchOwner("", String.class)

        assert !match
    }

    @Test void "test2"() {
        parameterDetour.detourOwner = "java.lang.String"
        parameterDetour.detourDesc = "detourDesc"
        parameterDetour.detourName = "detourName"
        MethodVisitor methodVisitorMock = mock(MethodVisitor.class)

        parameterDetour.rewrite(methodVisitorMock, 0, "java.lang.Object", Object.class, "name", "desc", true)

        verify(methodVisitorMock).visitMethodInsn(eq(184), eq("java.lang.String"), eq("detourName"), eq("detourDesc"), eq(false))
        verify(methodVisitorMock).visitMethodInsn(eq(0), eq("java.lang.Object"), eq("name"), eq("desc"), eq(true))
    }
}